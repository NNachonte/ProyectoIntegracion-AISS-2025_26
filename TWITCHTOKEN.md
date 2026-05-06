# TwitchMiner 🚀

Este proyecto es un servicio de Spring Boot configurado para interactuar con la API de Twitch. Sigue estos pasos para configurar tus credenciales de forma permanente en el sistema.

## 📋 Configuración Inicial Única

Realizaremos los pasos para obtener todos los datos antes de guardarlos definitivamente.

### 1. Obtención de Client ID y Secret
1. Entra en el [Twitch Developer Console](https://dev.twitch.tv/console).
2. Registra tu aplicación (Redirect URL: `http://localhost:8083`).
3. Copia el **Client ID**.
4. Genera y guarda un **Client Secret** (⚠️ Solo se muestra una vez).

### 2. Obtención del Access Token
Sin cerrar la terminal, ejecuta el siguiente comando sustituyendo tus datos reales (esto nos dará el token necesario para las llamadas a la API):

```bash
curl -X POST 'https://id.twitch.tv/oauth2/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d "client_id=TU_CLIENT_ID_AQUI" \
-d "client_secret=TU_CLIENT_SECRET_AQUI" \
```

De la respuesta JSON, copia el valor de `"access_token"`.

### 3. Configuración Permanente (Nano)
Ahora que tienes los tres datos (ID, Secret y Token), vamos a guardarlos en tu perfil de usuario para que no se borren nunca:

1. Abre el archivo de configuración:
   - En Linux/WSL: `nano ~/.bashrc`
   - En Mac: `nano ~/.zshrc`

2. Ve al final del archivo y pega estas tres líneas con tus datos:
   export TWITCH_CLIENT_ID="tu_client_id_aqui"
   export TWITCH_CLIENT_SECRET="tu_client_secret_aqui"
   export TWITCH_TOKEN="tu_access_token_aqui"

3. Guarda y sal: Presiona `Ctrl + O`, luego `Enter` para confirmar, y finalmente `Ctrl + X` para salir.

4. Actualiza tu terminal actual:
    `source ~/.bashr`  (o `source ~/.zshrc`)

---

## ✅ Verificación y Tests
Para confirmar que la configuración es correcta:

1. Inicia la aplicación.
2. Realiza los tests de TwitchMiner importando el archivo [postman-collection-miners.json](postman-collection-miners.json) en Postman.
3. Ejecuta la colección; **si no devuelve ningún error, es que la configuración se ha realizado correctamente.** :)