# Proyecto Base de Datos No Relacionales (MongoDB)

## 💡 Idea del proyecto
Este proyecto desarrolla un sistema que **lee datos de sensores DHT11 conectados a un Arduino**, los **envía a través del puerto serie** a un programa en **Java**, que luego **almacena los datos en una base de datos MongoDB**.  

Posteriormente, otra aplicación en Java **recupera los datos y los muestra en una interfaz gráfica (Swing)**, permitiendo filtrarlos y visualizar temperaturas y humedades destacadas con colores.

---

## ⚙️ Componentes necesarios
- **Arduino Uno:** microcontrolador principal.  
- **2 Sensores DHT11:** miden temperatura y humedad.  
- **2 Resistencias de 10 kΩ:** necesarias para el correcto funcionamiento de los DHT11.  
- **Protoboard:** facilita las conexiones sin soldar.  
- **Cables Dupont y USB:** para conexión de sensores y comunicación con el ordenador.  

---

## 🛠️ Herramientas utilizadas
- **Eclipse:** entorno de desarrollo (IDE) para programar en Java.  
- **Arduino IDE:** entorno para programar el microcontrolador Arduino.  
- **MongoDB:** base de datos NoSQL utilizada para almacenar los datos.  

**Lenguajes empleados:**
- **Java:** lógica del sistema, conexión con MongoDB y GUI con Swing.  
- **Arduino (C/C++):** control de sensores y envío de datos por puerto serie.  

---

## 🧩 Estructura del código

### 1. Arduino – `RecibirDatos.ino`
- Lee los datos de temperatura y humedad desde dos sensores DHT11.  
- Envía los datos en formato **JSON** por el puerto serie cada 2 segundos.  
- Incluye comprobaciones para evitar valores nulos.  

---

### 2. Java – `GuardarSensoresMongo.java`
**Función:** recibe los datos del Arduino, los procesa y los guarda en MongoDB.  

**Dependencias principales:**
- `com.fazecast.jSerialComm.SerialPort` → lectura del puerto serie.  
- `com.google.gson` → procesamiento de JSON.  
- `com.mongodb.client` y `org.bson.Document` → conexión e inserción en MongoDB.  

**Proceso:**
1. Conexión con MongoDB (`localhost:27017`).  
2. Creación de colecciones `Sensor1` y `Sensor2`.  
3. Lectura continua del puerto serie.  
4. Validación y conversión del JSON recibido.  
5. Inserción de los datos (temperatura, humedad y fecha/hora).  
6. Manejo de errores y cierre de recursos.  

---

### 3. Java – `ObtenerSensoresMongo.java`
**Función:** obtiene los datos almacenados en MongoDB.  

**Pasos:**
1. Conexión a la base de datos `Sensores`.  
2. Acceso a las colecciones `Sensor1` y `Sensor2`.  
3. Lectura de los documentos y almacenamiento en una lista.  
4. Cierre de conexión y retorno de datos para su visualización.  

---

### 4. Java – `MostrarSensores.java`
**Función:** muestra los datos en una interfaz gráfica con Swing.  

**Características:**
- Muestra una tabla con las columnas: **Sensor, Temperatura, Humedad, Fecha/Hora**.  
- Permite **filtrar por sensor** usando un `JComboBox` y un botón de filtrado.  
- Resalta:
  - Temperaturas más altas.  
  - Temperaturas más bajas.  
  - Humedades más altas.  
  - Humedades más bajas.  

**Clases adicionales:**
- `ColorTemperatureRenderer`: cambia colores de temperatura.  
- `ColorHumidityRenderer`: cambia colores de humedad.  

---

## 🎯 Finalidad del proyecto
El sistema permite:
- Monitorear en tiempo real las lecturas de múltiples sensores.  
- Almacenar los datos de manera estructurada en MongoDB.  
- Consultar y visualizar la información en una interfaz clara e interactiva.  

Este proyecto demuestra la integración entre **hardware (Arduino)**, **software (Java)** y **bases de datos NoSQL (MongoDB)**, aplicando conceptos de programación, electrónica y persistencia de datos.

---



