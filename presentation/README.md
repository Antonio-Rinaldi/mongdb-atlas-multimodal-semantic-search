# MongoDB Atlas Multimodal Semantic Search - Presentazione

## Come utilizzare la presentazione

1. Aprire `index.html` in un browser moderno
2. Utilizzare le frecce della tastiera per navigare tra le slide
3. Premere 'F' per visualizzare a schermo intero
4. Premere 'ESC' per vedere una panoramica di tutte le slide

## Struttura della presentazione

La presentazione segue la seguente struttura:
1. Introduzione alla Ricerca Semantica Multimodale con MongoDB Atlas
2. Cos'è una ricerca semantica?
3. Indici vettoriali: struttura e creazione (Atlas, Compass, Mongosh)
4. Tipi di ricerca vettoriale (ENN vs ANN)
5. Struttura di una Aggregation Pipeline per ricerca vettoriale
6. Input multimodale e gestione con Spring AI
7. Demo: struttura del dato, flussi di ingestion e ricerca semantica
8. Architettura RAG (Retrieval Augmented Generation)
9. Demo: RAG

## Demo 

### Prerequisites
- Docker and Docker Compose
- Python 3.x
- Maven
- Java 17 +
- Ollama

### Setup

Fa partire un'istanza locale di mongodb atlas e crea un indice per la ricerca vettoriale
```shell
# cd to project root
cd ..
# Start docker compose with local mongodb-atlas instance
docker-compose -f .ci/docker-compose.yml up -d
# Create vector search index
.ci/scripts/index/create_mongodb_atlas_vector_search_index.sh
```

Fa partire ollama
```shell
# cd to project root
cd ..
# Start ollama server
ollama serve
```

Pulla i large language models necessari per il funzionamento dell'applicazione.
I modelli che ho proposto qui sotto sono molto piccoli e potrebbero non dare grandissimi risultati.
In base alla potenza delle vostre macchine potete scaricare modelli più grandi e performanti.
Come modello per gestire l'input multimodale potete usare: gemma3:4b, gemma3:12b o gemma3:27b
Come modello per fare RAG potete usare: deepseek-r1:7b, deepseek-r1:14b, deepseek-r1:32b o qwq:32b
```shell
# cd to project root
cd ..
# Pull embedding model
ollama pull bge-m3:567m
# Pull the model used to handle multimodal inputs
ollama pull gemma3:1b
# Pull the model used for RAG 
ollama pull deepseek-r1:1.5b
```

Builda e fa partire l'applicazione
```shell
# cd to project root
cd ..
# Build the application
mvn clean package
# Start the application
java -jar -Dspring.profiles.active=local target/commercial-activities-1.0.0-SNAPSHOT.jar
```

Fa partire uno script per popolare il database (potrebbe richiedere fino a 15-20 minuti)
```shell
# cd to project root
cd ..
# Create virtual environment
python -m venv .venv
# Activate virtual environment
source .venv/bin/activate
# install required packages
pip install requests
# Ingest commercial activities into the database
python .ci/scripts/init/generate_activities.py
```

### Ricerca semantica

#### Esempio 1 - Formato di input: Testo

```shell
curl -X 'POST' \
  'http://localhost:8080/api/v1/commercial-activities/search?town=Roma&numberOfResults=10' \
  -H 'accept: application/json' \
  -H 'Content-Type: multipart/form-data' \
  -F 'text=I would really like to drink a beer, where should i go?' \
  -F 'media=string' | json_pp
```

#### Esempio 2 - Formato di input: Testo
```shell
curl -X 'POST' \
'http://localhost:8080/api/v1/commercial-activities/search?town=Roma&numberOfResults=10' \
-H 'accept: application/json' \
-H 'Content-Type: multipart/form-data' \
-F 'text=I would really like to buy some chocolate, where should i go?' \
-F 'media=string' | json_pp
```

### Esempio 3 - Formato di input: Testo
```shell
curl -X 'POST' \
  'http://localhost:8080/api/v1/commercial-activities/search?town=Roma&numberOfResults=10' \
  -H 'accept: application/json' \
  -H 'Content-Type: multipart/form-data' \
  -F 'text=I would really like to find an activity that sells both beer and chocolate, where should i go??' \
  -F 'media=string' | json_pp
```

### Esempio 4 - Formato di input: Testo + Immagine
```shell
curl -X 'POST' \
  'http://localhost:8080/api/v1/commercial-activities/search?town=Roma&numberOfResults=10' \
  -H 'accept: application/json' \
  -H 'Content-Type: multipart/form-data' \
  -F 'text=I would really like to find an activity that sells the content of the image i just sent you, where should i go?' \
  -F 'media=@./examples/Beer.png;type=image/png' | json_pp
```

### Esempio 5 - Formato di input: Testo + Immagine
```shell
curl -X 'POST' \
  'http://localhost:8080/api/v1/commercial-activities/search?town=Roma&numberOfResults=10' \
  -H 'accept: application/json' \
  -H 'Content-Type: multipart/form-data' \
  -F 'text=I would really like to find an activity that sells the content of the image i just sent you, where should i go?' \
  -F 'media=@./examples/Panino.jpg;type=image/jpeg' | json_pp
```




