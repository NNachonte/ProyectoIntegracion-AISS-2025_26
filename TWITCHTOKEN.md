# TwitchMiner 🚀

Proyecto Spring Boot que consume la **API de Twitch Helix** para buscar canales y vídeos, y enriquecer los detalles con clips como comentarios.

## 🔑 Obtener Credenciales de Twitch

### 1. Crear una Aplicación en Twitch
1. Accede a [Twitch Developer Console](https://dev.twitch.tv/console)
2. Inicia sesión con tu cuenta de Twitch
3. Ve a **Applications** → **Register Your Application**
4. Rellena los datos:
   - **Application Name**: `TwitchMiner` (o el que prefieras)
   - **OAuth Redirect URL**: `http://localhost:8083`
   - **Application Category**: `Application Integration`
   - Acepta los términos y crea la aplicación
5. Copia el **Client ID** (se mostrará en la página de aplicación)

### 2. Generar Client Secret
1. En la página de la aplicación, haz clic en **Manage**
2. Ve a la pestaña **OAuth** o copia desde el panel principal
3. Haz clic en **Generate** junto a **Client Secret**
4. ⚠️ **Copia inmediatamente** el Client Secret (solo se muestra una vez)

### 3. Obtener Access Token (App Access Token)
Ejecuta este comando en tu terminal, sustituyendo `CLIENT_ID` y `CLIENT_SECRET`:

**Linux/Mac/WSL:**
```bash
curl -X POST https://id.twitch.tv/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=CLIENT_ID_HERE" \
  -d "client_secret=CLIENT_SECRET_HERE" \
  -d "grant_type=client_credentials"
```

**Windows PowerShell:**
```powershell
$body = @{
    client_id = "CLIENT_ID_HERE"
    client_secret = "CLIENT_SECRET_HERE"
    grant_type = "client_credentials"
} | ConvertTo-Json

Invoke-WebRequest -Uri https://id.twitch.tv/oauth2/token `
  -Method POST `
  -ContentType "application/json" `
  -Body $body | Select-Object -ExpandProperty Content | ConvertFrom-Json
```

Del resultado JSON, copia el valor de `access_token`.

## 💾 Guardar Credenciales de Forma Permanente

### Variables de Entorno (Recomendado)

**Linux/Mac/WSL:**
```bash
# Abre el archivo de configuración
nano ~/.bashrc  # o ~/.zshrc en Mac

# Al final, añade estas líneas:
export TWITCH_CLIENT_ID="your_client_id_here"
export TWITCH_TOKEN="your_access_token_here"

# Guarda (Ctrl+O, Enter, Ctrl+X) y recarga:
source ~/.bashrc  # o source ~/.zshrc
```

**Windows PowerShell (Permanente):**
```powershell
# Ejecuta como Administrador
[Environment]::SetEnvironmentVariable("TWITCH_CLIENT_ID", "your_client_id_here", "User")
[Environment]::SetEnvironmentVariable("TWITCH_TOKEN", "your_access_token_here", "User")

# Cierra y reabre PowerShell para que los cambios tengan efecto
# Verifica:
Write-Output $env:TWITCH_CLIENT_ID
Write-Output $env:TWITCH_TOKEN
```

---

## ✅ Verificación y Tests

Para confirmar que la configuración es correcta:

1. Inicia la aplicación.
2. Realiza los tests de TwitchMiner importando el archivo `postman-collection-miners.json` en Postman.
3. Ejecuta la colección; si no devuelve ningún error, es que la configuración se ha realizado correctamente. :)