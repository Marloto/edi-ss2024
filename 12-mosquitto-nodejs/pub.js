const client = require('mqtt').connect(process.env.MQTT_HOST || 'mqtt://localhost:1883');

client.on('connect', () => {
    setInterval(() => {
        client.publish(`sensor/sensor1/temp`, `${Math.random()}`)
    }, 1000)
});