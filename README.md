```
{
  "fields": [
    {
      # Tipo di indice
      "type": "vector",
      # Campo della collection in cui sono salvati gli enbeddings  
      "path": "<name_of_embedding_field>",
      # Dimensione dello spazio vettoriale
      "numDimensions": <number_of_dimension>,
      # Tipo di funzione con cui viene calcolata la distanza tra due punti ?
      "similarity": "euclidean | cosine | dotProduct",
      # Tipo di quantizzazione (meccanismo di riduzione delle dimensioni degli embedding)
      "quantization": "none | scalar | binary"
    },
    { 
      "path": "<field_used_for_filter>",
      "type": "filter"
    }
  ]
}
```