# Mongodb Atlas Hybrid Semantic Search

## Descrizione

## Definizione di un indice vettoriale

```json
{
  "name": "<name_of_the_index>",
  "definition": {
    "fields": [
      {
        "type": "vector",
        "path": "<name_of_embedding_field>",
        "numDimensions": <number_of_dimension>,
        "similarity": "euclidean | cosine | dotProduct",
        "quantization": "none | scalar | binary"
      },
      {
        "type": "filter"
        "path": "<field_used_for_filter>"
      }
    ]
  }
}

```
- `name` definisce il nome dell'indice
- `definition` contiene la definizione dell'indice
  - `fields` contiene i campi del documento su cui andrà ad insistere l'indice
    - `type` contiene il tipo del campo dell'indice, due valori sono possibili:
      - `vector`: i campi di questo tipo vengono usati per fare ricerca semantica
      - `filter`: i campi di questo tipo vengono usati per fare una ricerca esatta che va a filtrare il dataset di partenza prima della ricerca semantica
    - `path` in base al `type` ha due scopi diversi:
      - se il `type` == `vector` contiene il nome del campo in cui sono salvati gli embeddings
      - se il `type` == `filter` contiene il nome del campo su cui insiste la ricerca
    - `numDimensions` (solo `type` == `vector`) indica la dimensione dello spazio vettoriale su cui insiste la ricerca
    - `similarity` (solo `type` == `vector`) indica il tipo di funzione con cui è calcolata la distanza tra due punti dello spazio vettoriale, può avere tre possibili valori :
      - `euclidean` TODO
      - `cosine` TODO
      - `dotProduct` TODO
    - `quantization`(solo `type` == `vector`) permette di specificare un meccanismo per ridurre la dimensione degli embeddings, può avere tre possibili valori:
      - `none` TODO
      - `scalar` TODO
      - `binary` TODO

## Tipi di ricerca vettoriale

### Ricerca ENN

### Ricerca ANN

## Definizione di una aggregazione che insiste sull'indice

```json
[
  {
    "$vectorSearch": {
      "index": "<name_of_the_index>",
      "filter": {},
      "exact": "true | false",
      "numCandidates": "<number_of_candidates>",
      "path": "<Indexed_field_with_type_vector>",
      "queryVector": [
        "<embeddings_array_of_the_user_input>"
      ]
      "limit": "<number_of_results>",
    }
  },
  {
    "$project": {
      "<field-to-include>": 1,
      "<field-to-exclude>": 0,
      "score": {
        "$meta": "vectorSearchScore"
      }
    }
  }
]
```

- `$vectorSearch` descrive la query, deve essere il primo operatore della aggregation pipeline
  - `index` nome dell'indice vettoriale su cui insiste l'aggregation
  - `filter` descrive una query di matching esatto che va a ridurre il dataset preso in considerazione dalla ricerca semantica, i campi su cui insiste questa query devo essere definiti come `type` == `filter` nell'indice
  - `exact` dice se la ricerca deve prendere in esame tutto il dataset (ENN) o solo un numero predefinito dei candidati più vicini al valore del vettore di embedding in input (ANN)
  - `numCandidates` utilizzabile solo se `exact` == `false`, indica il numero dei candidati più vicini al valore del vettore di embedding in input che devono essere presi in considerazione per effettuare la ricerca semantica
  - `path` nome del campo del documento che contiene gli embeddings, deve essere definito nell'indice vettoriale con `type` == `vector`
  - `embeddings` embedding vector derivato dall'user input che verrà confrontato con gli embedding vectors presenti nei documenti del database
  - `limit` numero di risultati che verranno restituiti dalla ricerca
- `$project` descrive la proiezione eseguita sui risultati della ricerca vettoriale
  - `score` aggiunge al risultato un campo `score` che contiene il punteggio del risultato (quanto è vicino al vettore degli embedding che esprime l'user input)
