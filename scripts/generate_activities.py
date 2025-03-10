import argparse
import random
import requests
import string
import sys
import time
from datetime import datetime
from typing import List, Dict

# Business name components
prefixes = ["Ristorante", "Cafe", "Bar", "Trattoria", "Osteria", "Pizzeria", "Hotel", "Boutique", "Studio", "Centro", 
           "Palestra", "Libreria", "Enoteca", "Gelateria", "Pasticceria", "Mercato", "Galleria", "Teatro", "Cinema", "Spa"]
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

def generate_name() -> str:
    """Generate a random business name."""
    return f"{random.choice(prefixes)} {random.choice(names)}"

def generate_address() -> str:
    """Generate a random address."""
    return f"{random.choice(streets)}, {random.randint(1, 200)}"

def get_random_categories() -> List[str]:
    """Get 1-3 random categories."""
    category_type = random.choice(list(categories.keys()))
    num_categories = random.randint(1, 3)
    return random.sample(categories[category_type], min(num_categories, len(categories[category_type])))

def generate_description(name: str, selected_categories: List[str]) -> str:
    """Generate a description based on the business name and categories."""
    adjectives = ["eccellente", "rinomato", "accogliente", "elegante", "tradizionale", "moderno", "esclusivo"]
    features = ["servizio personalizzato", "atmosfera unica", "location strategica", "esperienza indimenticabile"]
    
    description = f"{random.choice(adjectives).capitalize()} {name.lower()} che offre {random.choice(features)}. "
    description += f"Specializzato in {', '.join(selected_categories)}. "
    description += f"{random.choice(features).capitalize()}."
    return description

def generate_activity() -> Dict:
    """Generate a single commercial activity."""
    name = generate_name()
    selected_categories = get_random_categories()
    
    return {
        "taxCode": generate_tax_code(),
        "name": name,
        "town": random.choice(towns),
        "address": generate_address(),
        "categories": selected_categories,
        "description": generate_description(name, selected_categories)
    }

def generate_batch(size: int = 100) -> List[Dict]:
    """Generate a batch of commercial activities."""
    return [generate_activity() for _ in range(size)]

def send_batch(activities: List[Dict], base_url: str = "http://localhost:8080") -> bool:
    """Send a batch of activities to the API."""
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

def test_server_connection(base_url: str = "http://localhost:8080") -> bool:
    """Test if the server is running and accessible."""
    try:
        print(f"\nTesting server connection at {base_url}...")
        response = requests.get(base_url)
        print(f"Server is accessible (status code: {response.status_code})")
        return True
    except requests.exceptions.ConnectionError:
        print(f"\nError: Could not connect to server at {base_url}")
        print("Please ensure the server is running and try again.")
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