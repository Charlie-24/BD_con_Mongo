#include <DHT.h>
#include <ArduinoJson.h>

const int sensor1Pin = 2;
const int sensor2Pin = 3;

DHT dht1(sensor1Pin, DHT11);  
DHT dht2(sensor2Pin, DHT11);  

void setup() {
    Serial.begin(9600);
    dht1.begin();  
    dht2.begin();  
}

void loop() {
    
    float temp1 = dht1.readTemperature();  
    float hum1 = dht1.readHumidity();     
    float temp2 = dht2.readTemperature();  
    float hum2 = dht2.readHumidity();     

    if (isnan(temp1) || isnan(hum1) || isnan(temp2) || isnan(hum2)) {
        Serial.println("Error al leer los sensores");
        return;
    }

    StaticJsonDocument<200> jsonDoc;
    JsonObject s1 = jsonDoc.createNestedObject("sensor_1");
    s1["temperatura"] = temp1;
    s1["humedad"] = hum1;

    JsonObject s2 = jsonDoc.createNestedObject("sensor_2");
    s2["temperatura"] = temp2;
    s2["humedad"] = hum2;

    String jsonString;
    serializeJson(jsonDoc, jsonString);
    Serial.println(jsonString);

    delay(2000);  
}
