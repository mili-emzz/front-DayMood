https://day-mood.vercel.app/

# DayMood - Frontend Móvil

## Descripción

DayMood es una aplicación móvil orientada al bienestar emocional que permite a los usuarios registrar, monitorear y analizar sus emociones y hábitos diarios. Este repositorio contiene el frontend de la aplicación móvil, desarrollado para Android utilizando Kotlin y Jetpack Compose.

El proyecto surge como respuesta a la creciente necesidad de herramientas digitales accesibles que faciliten la gestión de la salud mental, especialmente en jóvenes, permitiendo transformar experiencias emocionales en información estructurada y analizable.

## Problema que soluciona

Actualmente, muchas personas presentan dificultades para:

* Identificar y comprender sus emociones
* Mantener constancia en el registro de hábitos
* Detectar patrones emocionales a lo largo del tiempo
* Acceder a herramientas estructuradas de autocuidado
* Sentirse acompañadas en su proceso emocional

A pesar del crecimiento de aplicaciones digitales, existe una carencia de soluciones que integren registro, análisis y acompañamiento en una sola plataforma.

## Propuesta de solución

DayMood ofrece una aplicación móvil que integra:

* Registro diario de emociones y hábitos
* Personalización de emociones mediante contenido propio
* Visualización de estadísticas semanales
* Foro segmentado por edad y categorías
* Evaluación del progreso emocional mediante encuestas tipo Likert

El frontend móvil es la capa encargada de la interacción directa con el usuario, proporcionando una interfaz intuitiva, accesible y enfocada en la experiencia de uso.

## Funcionalidades principales

### Registro diario

Permite al usuario seleccionar una emoción y uno o más hábitos para generar un registro estructurado del día.

### Personalización de emociones

El usuario puede crear emociones personalizadas, incluyendo nombre, categoría e imagen.

### Estadísticas semanales

Visualización de tendencias emocionales a partir de los registros realizados.

### Foro

Espacio de interacción donde los usuarios pueden publicar y comentar dentro de categorías y rangos de edad.

### Encuestas

Sistema basado en escala Likert para medir dimensiones de la inteligencia emocional.

## Tecnologías utilizadas

### Kotlin

Lenguaje principal para el desarrollo de la aplicación móvil en Android. Permite un desarrollo moderno, seguro y eficiente.

### Jetpack Compose

Framework de UI declarativa utilizado para construir interfaces modernas en Android.

* Manejo de estados
* Componentes reutilizables
* Navegación entre pantallas

### Android Studio

Entorno de desarrollo utilizado para la construcción, ejecución y pruebas de la aplicación.

## Arquitectura del sistema

El frontend móvil forma parte de una arquitectura de tres capas con separación de responsabilidades:

### Capa de presentación (este repositorio)

* Aplicación Android desarrollada en Kotlin
* Manejo de navegación, estados y UI
* Consumo de la API REST mediante HTTP

### Capa de lógica de negocio

* Backend desarrollado con Node.js, Express y TypeScript
* Exposición de endpoints REST
* Validaciones y reglas del sistema

### Capa de datos

* PostgreSQL como base de datos relacional
* Prisma ORM para acceso tipado a datos

### Servicios externos

* Firebase Authentication: gestión de usuarios y autenticación mediante tokens JWT
* Firebase Storage: almacenamiento de imágenes de emociones personalizadas

## Comunicación con el backend

La aplicación móvil consume una API REST mediante solicitudes HTTP autenticadas.

Flujo general:

1. El usuario inicia sesión mediante Firebase Authentication
2. Se obtiene un token JWT
3. El token se envía en cada petición al backend
4. El backend valida el token y procesa la solicitud
5. Se retorna la información correspondiente al frontend

## Ejecución del proyecto

Para ejecutar el proyecto:

1. Abrir el repositorio en Android Studio
2. Configurar un emulador o dispositivo físico
3. Ejecutar la aplicación desde el entorno de desarrollo

## Consideraciones legales y de privacidad

El sistema está diseñado considerando normativas relacionadas con:

* Protección de datos personales
* Consentimiento informado
* Uso de datos sensibles (emociones y hábitos)
* Derechos ARCO (acceso, rectificación, cancelación y oposición)

Asimismo, la aplicación:

* No realiza diagnósticos clínicos
* No sustituye atención psicológica profesional
* Utiliza los datos únicamente con fines de monitoreo y análisis personal

## Contexto académico

Este proyecto fue desarrollado como parte del programa de Ingeniería en Tecnologías de la Información e Innovación Digital de la Universidad Politécnica de Chiapas.

Se implementó bajo una metodología ágil (Scrum), permitiendo un desarrollo iterativo, incremental y enfocado en el usuario.

## Créditos

Integrantes del proyecto:

* Emilia Gómez
* Nadia Guerra
* Enmma Reyes
