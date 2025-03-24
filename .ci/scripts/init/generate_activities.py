import argparse
import os
import random
import requests
import string
import sys
import time
from datetime import datetime
from typing import List, Dict, Tuple

# Business categories and their appropriate prefixes
category_prefixes = {
    "food_dining": ["Ristorante", "Trattoria", "Osteria", "Pizzeria"],
    "beverages_nightlife": ["Bar", "Cafe", "Enoteca"],
    "food_services": ["Pasticceria", "Gelateria", "Mercato"],
    "accommodation": ["Hotel"],
    "shopping": ["Boutique", "Galleria"],
    "health_beauty": ["Spa"],
    "fitness_sports": ["Palestra", "Centro"],
    "entertainment": ["Teatro", "Cinema"],
    "professional": ["Studio"],
    "education": ["Libreria"]
}

# Business name components
names = ["Milano", "Italia", "Centrale", "Nuovo", "Bella", "Verde", "Arte", "Sole", "Luna", "Mare", 
         "Stella", "Rosa", "Oro", "Blu", "Rosso", "Bianco", "Nero", "Piccolo", "Grande", "Antica"]

# Street names
streets = ["Via Roma", "Via Milano", "Via Garibaldi", "Via Dante", "Via Mazzini", "Corso Italia", "Via Verdi", 
          "Via Vittorio Emanuele", "Via Leonardo da Vinci", "Via Marconi", "Via delle Rose", "Via dei Fiori",
          "Via della Repubblica", "Via XX Settembre", "Via Cavour", "Via Colombo", "Via Galilei", "Via Pascoli"]

# Categories from the defined list
categories = {
    "food_dining": [
        "restaurant", "italian", "chinese", "japanese", "indian", "mexican", "thai", "vietnamese", "mediterranean",
        "french", "spanish", "greek", "american", "fusion", "seafood", "steakhouse", "pizzeria", "sushi", "ramen",
        "burger", "vegetarian", "vegan", "gluten-free", "organic", "farm-to-table", "fine-dining", "casual-dining",
        "fast-food", "food-truck", "buffet"
    ],
    "beverages_nightlife": [
        "bar", "wine-bar", "craft-beer", "pub", "cocktail-bar", "sports-bar", "nightclub", "lounge", "brewery",
        "winery", "coffee-shop", "tea-house", "juice-bar", "smoothie-bar", 
    ],
    "food_services": [
        "bakery", "pastry-shop", "ice-cream", "deli", "butcher", "fishmonger", "greengrocer", "supermarket",
        "convenience-store", "specialty-food", "organic-store", "liquor-store", "farmers-market", "food-hall"
    ],
    "accommodation": [
        "hotel", "boutique-hotel", "bed-breakfast", "hostel", "resort", "apartment-hotel", "vacation-rental",
        "camping", "guesthouse"
    ],
    "shopping": [
        "fashion", "clothing", "shoes", "accessories", "jewelry", "luxury", "vintage", "electronics", "bookstore",
        "music-store", "toy-store", "gift-shop", "department-store", "mall", "outlet"
    ],
    "health_beauty": [
        "spa", "salon", "barbershop", "beauty-store", "cosmetics", "pharmacy", "wellness-center", "massage",
        "nail-salon"
    ],
    "fitness_sports": [
        "gym", "fitness-center", "yoga-studio", "pilates", "crossfit", "martial-arts", "swimming-pool",
        "sports-center", "dance-studio"
    ],
    "entertainment": [
        "theater", "cinema", "museum", "art-gallery", "concert-hall", "live-music", "comedy-club", "bowling",
        "arcade", "karaoke"
    ],
    "professional": [
        "coworking", "office", "bank", "insurance", "real-estate", "travel-agency", "printing", "laundry",
        "tailor", "repair-shop"
    ],
    "education": [
        "school", "university", "language-school", "music-school", "art-school", "cooking-school", "dance-school",
        "tutoring", "library", "workshop"
    ]
}

# Italian cities
towns = ["Roma", "Milano", "Napoli", "Torino", "Firenze", "Bologna", "Venezia", "Verona", "Genova", "Palermo"]

def generate_tax_code() -> str:
    """Generate a random Italian-style tax code."""
    letters = ''.join(random.choices(string.ascii_uppercase, k=6))
    numbers = ''.join(random.choices(string.digits, k=5))
    check_letter = random.choice(string.ascii_uppercase)
    return f"{letters}{numbers}{check_letter}"

def generate_business_info() -> Tuple[str, str, List[str]]:
    """Generate a coherent business name and its matching categories."""
    # First select a random category type
    category_type = random.choice(list(categories.keys()))
    # Get the appropriate prefixes for this category type
    possible_prefixes = category_prefixes[category_type]
    # Select a random prefix from the appropriate ones
    prefix = random.choice(possible_prefixes)
    # Generate the full name
    name = f"{prefix} {random.choice(names)}"
    # Get 1-3 matching categories from the same category type
    num_categories = random.randint(1, 3)
    selected_categories = random.sample(categories[category_type], min(num_categories, len(categories[category_type])))
    
    return name, category_type, selected_categories

def generate_address() -> str:
    """Generate a random address."""
    return f"{random.choice(streets)}, {random.randint(1, 200)}"

def generate_description(name: str, category_type: str, selected_categories: List[str]) -> str:
    """Generate a description based on the business name and categories."""
    # Customize adjectives and features based on category type
    category_specific_adjectives = {
        "food_dining": ["rinomato", "gustoso", "accogliente"],
        "beverages_nightlife": ["vivace", "alla moda", "elegante"],
        "food_services": ["fresco", "genuino", "artigianale"],
        "accommodation": ["confortevole", "lussuoso", "accogliente"],
        "shopping": ["elegante", "esclusivo", "alla moda"],
        "health_beauty": ["rilassante", "professionale", "moderno"],
        "fitness_sports": ["attrezzato", "professionale", "moderno"],
        "entertainment": ["divertente", "coinvolgente", "vivace"],
        "professional": ["professionale", "affidabile", "esperto"],
        "education": ["stimolante", "educativo", "professionale"]
    }
    
    category_specific_features = {
        "food_dining": ["cucina raffinata", "ingredienti di prima qualità", "ambiente accogliente"],
        "beverages_nightlife": ["atmosfera unica", "selezione premium", "musica dal vivo"],
        "food_services": ["prodotti freschi", "qualità artigianale", "servizio personalizzato"],
        "accommodation": ["comfort moderno", "posizione centrale", "servizio premium"],
        "shopping": ["ultime tendenze", "prodotti esclusivi", "consulenza personalizzata"],
        "health_beauty": ["trattamenti personalizzati", "prodotti di qualità", "ambiente rilassante"],
        "fitness_sports": ["attrezzature moderne", "istruttori qualificati", "programmi personalizzati"],
        "entertainment": ["programmazione variegata", "esperienza coinvolgente", "eventi speciali"],
        "professional": ["servizio professionale", "consulenza esperta", "soluzioni personalizzate"],
        "education": ["programmi personalizzati", "insegnanti qualificati", "ambiente stimolante"]
    }
    
    adjectives = category_specific_adjectives.get(category_type, ["eccellente", "rinomato", "accogliente"])
    features = category_specific_features.get(category_type, ["servizio personalizzato", "location strategica"])
    
    description = f"{random.choice(adjectives).capitalize()} {name.lower()} che offre {random.choice(features)}. "
    description += f"Specializzato in {', '.join(selected_categories)}. "
    description += f"{random.choice(features).capitalize()}."
    return description

def generate_activity() -> Dict:
    """Generate a single commercial activity."""
    name, category_type, selected_categories = generate_business_info()
    
    return {
        "taxCode": generate_tax_code(),
        "name": name,
        "town": random.choice(towns),
        "address": generate_address(),
        "categories": selected_categories,
        "description": generate_description(name, category_type, selected_categories)
    }

def generate_batch(size: int = 100) -> List[Dict]:
    """Generate a batch of commercial activities."""
    return [generate_activity() for _ in range(size)]

def send_batch(activities: List[Dict]) -> bool:
    """Send a batch of activities to the API."""
    base_url = os.getenv("API_BASE_URL", "http://localhost:8080")
    url = f"{base_url}/api/v1/commercial-activities/batch"
    headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
    
    try:
        response = requests.post(url, json=activities, headers=headers)
        response.raise_for_status()
        return True
    except requests.exceptions.RequestException as e:
        print(f"\nError sending batch: {str(e)}")
        return False

def test_server_connection() -> bool:
    """Test if the server is running and accessible."""
    base_url = os.getenv("API_BASE_URL", "http://localhost:8080")
    try:
        print(f"\nTesting server connection at {base_url} (API_BASE_URL environment variable)...")
        response = requests.get(base_url)
        print(f"Server is accessible (status code: {response.status_code})")
        return True
    except requests.exceptions.ConnectionError:
        print(f"\nError: Could not connect to server at {base_url}")
        print("Please ensure the server is running and the API_BASE_URL environment variable is set correctly.")
        return False

def main():
    parser = argparse.ArgumentParser(description='Generate and send commercial activities to the API')
    parser.add_argument('--test', action='store_true', help='Run in test mode with 2 activities')
    parser.add_argument('--batch-size', type=int, default=100, help='Batch size for sending activities (default: 100)')
    parser.add_argument('--total', type=int, default=10000, help='Total number of activities to generate (default: 10000)')
    args = parser.parse_args()

    if not test_server_connection():
        sys.exit(1)

    if args.test:
        print("\nRunning in test mode (2 activities)...")
        test_batch = generate_batch(2)
        success = send_batch(test_batch)
        if not success:
            print("\nTest batch failed.")
            sys.exit(1)
        print("\nTest successful! Run without --test to generate all activities.")
        sys.exit(0)

    total_activities = args.total
    batch_size = args.batch_size
    num_batches = (total_activities + batch_size - 1) // batch_size

    start_time = time.time()
    activities_sent = 0
    print(f"\nGenerating and sending {total_activities:,} activities in batches of {batch_size}...")

    for batch_num in range(num_batches):
        # For the last batch, adjust size if needed
        current_batch_size = min(batch_size, total_activities - activities_sent)
        batch = generate_batch(current_batch_size)
        
        # Calculate progress
        progress = (activities_sent / total_activities) * 100
        elapsed = time.time() - start_time
        rate = activities_sent / elapsed if elapsed > 0 else 0
        eta = (total_activities - activities_sent) / rate if rate > 0 else 0
        
        # Print progress
        print(f"\nBatch {batch_num + 1}/{num_batches} "
              f"({progress:.1f}% complete, "
              f"sending {len(batch)} activities)")
        print(f"Rate: {rate:.0f} activities/sec, "
              f"ETA: {datetime.fromtimestamp(time.time() + eta).strftime('%H:%M:%S')}")
        
        if not send_batch(batch):
            print(f"\nFailed at batch {batch_num + 1}. {activities_sent:,} activities were sent successfully.")
            sys.exit(1)
        
        activities_sent += len(batch)

    total_time = time.time() - start_time
    final_rate = activities_sent / total_time
    
    print(f"\nSuccess! Sent {activities_sent:,} activities in {total_time:.1f} seconds")
    print(f"Average rate: {final_rate:.0f} activities/second")

if __name__ == "__main__":
    main()