# Microservicio de Vehículos

Microservicio de vehículos que obtiene la información de un vehículo por la placa.

Comando Docker para construir la imagen de docker en Azure ACR
------------------------------------------------------------------
```az acr login --name vehiclemsjava```

```docker buildx build --platform linux/amd64,linux/arm64 -t vehiclemsjava.azurecr.io/vehicle-ms:latest --push .```

Comandos Kubernetes para levantar contenedor desde CLI de Azure
------------------------------------------------------------------
### 1. Obtener credenciales de conexión al AKS aks-vehicle-platform
```az aks get-credentials --resource-group rg-vehicle-platform --name vehicle-score-platform```

### 2. Crear el namespace
```kubectl apply -f namespace.yaml```

### 3. Aplicar ConfigMap
```kubectl apply -f configmap.yaml```

### 4. Aplicar Secret
Tener presente que se debe setear la variable de entorno AZURE_SERVICE_BUS_ENDPOINT con el Endpoint
del service bus encriptado en base64 antes de ejecutar el comando para crear el secreto.

- Formato Endpoint Service Bus:
  - ```Endpoint=sb://sb-<nombre-servicebus>.servicebus.windows.net/;SharedAccessKeyName=<shared-access-key-name>;SharedAccessKey=<shared-access-key>```

- Endpoint Encriptado Ejemplo:
  - ```MW5scG92bnQ9c2I6Ly9zYi12ZuhpY2xlLXBsYXRgb3JtHnjlcnZpY2VidXMud2luZG93cy5rZXQvO1NoYXJlZEFjY2Vzc0tleU5hbWU9Um9vdE1hbmFnZVNoYXJlZEFjY2Vzc0tleTtTaGFyZWRBY2Nlc3NLZXk9VzZtbVZGYWxSWmhBZ2xDY1NVRWx5VCtuQlVSYVpZRkUxK0FTYktOZkRUdz0=```

- Comando para crear el secret:
  - ```kubectl apply -f secrets.yaml```

### 5. Desplegar el servicio
```kubectl apply -f deployment.yaml```

### 6. Crear el servicio
```kubectl apply -f service.yaml```

### 7. Aplicar Horizontal Pods Auto-Scaler
```kubectl apply -f hpa.yaml```

### 8. Configurar el ingress
```kubectl apply -f ingress.yaml```