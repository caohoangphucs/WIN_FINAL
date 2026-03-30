# WIN_FINAL

## Requirements
Java 21 AND docker

### 1. start db and build app
```bash
docker compose up -d
```

### 2. RUN
#### 2.1 Linux
```bash
./mvnw compile exec:java -Dexec.mainClass="com.example.winfinal.Main"
```
#### 2.2 Win
```bash
mvnw.cmd compile exec:java -Dexec.mainClass="com.example.winfinal.Main"
```
