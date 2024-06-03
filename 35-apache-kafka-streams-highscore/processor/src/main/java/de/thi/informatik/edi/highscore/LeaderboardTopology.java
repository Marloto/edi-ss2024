package de.thi.informatik.edi.highscore;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

import de.thi.informatik.edi.highscore.model.Enriched;
import de.thi.informatik.edi.highscore.model.Leaderboard;
import de.thi.informatik.edi.highscore.model.Player;
import de.thi.informatik.edi.highscore.model.Product;
import de.thi.informatik.edi.highscore.model.ScoreEvent;
import de.thi.informatik.edi.highscore.model.ScoreWithPlayer;

public class LeaderboardTopology {
    public static Topology build() {
        StreamsBuilder builder = new StreamsBuilder();

        // Stream / Table incl. Serdes
        // -> Anzahl der Partitionen bereits synchronisiert
        // Score Events (mit Partitionen, nur teil bekannt, Key hier nicht weiter bekannt)
        KStream<String, ScoreEvent> scoreEvents = builder.stream(Configurator.SCORE_EVENTS, 
        		Consumed.with(Serdes.String(), JsonSerdes.scoreEvent()));

        // Player (mit Partitionen, nur teil bekannt, und PlayerId ist Key)
        KTable<String, Player> players = builder.table(Configurator.PLAYERS, 
        		Consumed.with(Serdes.String(), JsonSerdes.player()));
        
        // Products (alle Spiele bekannt)
        GlobalKTable<String, Product> products = builder.globalTable(Configurator.PRODUCTS, 
        		Consumed.with(Serdes.String(), JsonSerdes.product()));
        
        // Rekeying, aus dem Topic "score-events" und den Daten, wird ein Key ausgewählt
        // -> hier playerId
        // -> mit dem Aufruf von selectKey wird das Ergebnis in einem Topic in Kafka zurückgespielt
        // -> und dann neu "consumiert" von dem passenden Consumer
        KStream<String, ScoreEvent> scoreEventsWithPlayerId = scoreEvents
        		.selectKey((key, value) -> value.getPlayerId().toString());
        
        
        // Join
        KStream<String, ScoreWithPlayer> scoreWithPlayer = scoreEventsWithPlayerId.join(players, ScoreWithPlayer::new, 
        		Joined.with(Serdes.String(), JsonSerdes.scoreEvent(), JsonSerdes.player()));
        
        KStream<String, Enriched> joined = scoreWithPlayer.join(products, 
        		(leftKey, scoreWith) -> String.valueOf(scoreWith.getScoreEvent().getProductId()), 
        		Enriched::new);

        // GroupBy, Aggregate and To
        
        // Gruppieren nach Produkt-ID um einen KStream zu haben, der nur ein Produkt betrachtet
        KGroupedStream<String, Enriched> groupBy = joined.groupBy((key, value) -> value.getProductId().toString(), 
        		Grouped.with(Serdes.String(), JsonSerdes.enriched()));
        
        // Aggregate
        // vgl. reduce-Operator bei Rx? Zwei Werte verwendet und ein Ergebnis erzeugt, 
        //      Ergebnis der Reduce zzgl. aktuelles
        // aggregate kann nur über groupBy genutzt werden
        KTable<String, Leaderboard> aggregated = groupBy.aggregate(Leaderboard::new, 
        		(key, value, state) -> state.update(value), 
        		Materialized.with(Serdes.String(), JsonSerdes.leaderboard()));
        
        // Ergebnis in einem Topic veröffentlichen
        aggregated.toStream().to(Configurator.LEADER_BOARDS);


        return builder.build();
    }
}
