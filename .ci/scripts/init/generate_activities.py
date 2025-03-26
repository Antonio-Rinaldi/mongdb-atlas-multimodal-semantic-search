import argparse
import json
import logging
import os
import random
import re
import requests
import string
import sys
import time
from datetime import datetime
from typing import List, Dict, Tuple

# Configure logging
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

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
    
    logger.debug(f"Generated business info - Name: {name}, Type: {category_type}, Categories: {selected_categories}")
    return name, category_type, selected_categories

def generate_address() -> str:
    """Generate a random address."""
    address = f"{random.choice(streets)}, {random.randint(1, 200)}"
    logger.debug(f"Generated address: {address}")
    return address

def get_ollama_description(document: Dict) -> str:
    """Get a description from ollama based on the document information."""
    OLLAMA_URL = "http://localhost:11434/api/generate"
    
    try:
        # Log the request
        logger.debug(f"Sending request to ollama API at {OLLAMA_URL}")
        logger.debug(f"Request payload: {json.dumps(document, indent=2)}")
        
        # Build the prompt
        prompt = (
            "You are an expert copywriter. Write a brief, elegant description for this commercial activity that:\n"
            "- Contains 2-3 concise sentences\n"
            "- Mentions the location and main characteristics\n"
            "- Describes key products or services\n"
            "Generate only the description, no additional notes or commentary.\n\n"
            f"{json.dumps(document, indent=2)}"
        )
        
        # Make the request
        response = requests.post(
            OLLAMA_URL,
            json={
                "model": "gemma3:4b",
                "prompt": prompt,
                "stream": True
            },
            stream=True,
            timeout=30
        )
        
        # Log the response status
        logger.debug(f"Ollama API response status: {response.status_code}")
        response.raise_for_status()
        
        # Process the streaming response
        description_parts = []
        in_description = False
        thinking_mode = False
        
        for line in response.iter_lines(decode_unicode=True):
            if not line:
                continue
                
            try:
                # Parse each line as a separate JSON object
                chunk = json.loads(line)
                logger.debug(f"Response chunk: {chunk}")
                
                # Get the response text from the chunk
                if 'response' in chunk:
                    text = chunk['response']
                    logger.debug(f"Processing response text: '{text}'")
                    
                    # Handle thinking mode markers
                    if '<think>' in text:
                        thinking_mode = True
                        logger.debug("Entering thinking mode")
                        continue
                    if '</think>' in text:
                        thinking_mode = False
                        in_description = True
                        logger.debug("Exiting thinking mode")
                        continue
                    
                    # Log filtering decisions
                    if thinking_mode:
                        logger.debug(f"Skipping text in thinking mode: '{text}'")
                        continue
                        
                    if any(marker in text.lower() for marker in
                            ['think', 'okay', 'let me', 'alright', 'first', '/think']):
                        logger.debug(f"Skipping text with thinking markers: '{text}'")
                        continue
                    
                    # Only append text if we're not in thinking mode
                    if not thinking_mode:
                        logger.debug(f"Adding to description: '{text}'")
                        description_parts.append(text)
                    
                # Check if this is the final chunk
                if chunk.get('done', False):
                    break
                    
            except json.JSONDecodeError as e:
                logger.error(f"Error parsing response chunk: {e}")
                continue
                
        # Join all parts and clean up
        raw_description = ''.join(description_parts).strip()
        logger.debug(f"Raw joined description: '{raw_description}'")
        
        # Clean up any remaining markers and thinking process
        description = re.sub(r'<[^>]+>', '', raw_description)  # Remove XML-like tags
        logger.debug(f"After removing XML tags: '{description}'")

        # Fix spacing around punctuation
        description = description.strip()  # Remove leading/trailing whitespace
        logger.debug(f"Final cleaned description: '{description}'")
        
        logger.debug(f"Generated description: {description}")
        
        if not description:
            raise ValueError("No valid description generated")
            
        return description
        
    except requests.exceptions.Timeout:
        logger.error("Timeout while calling ollama API")
        return generate_fallback_description(document)
    except requests.exceptions.ConnectionError:
        logger.error(f"Connection error while calling ollama API at {OLLAMA_URL}")
        return generate_fallback_description(document)
    except (requests.exceptions.RequestException, ValueError, json.JSONDecodeError) as e:
        logger.error(f"Error calling ollama: {str(e)}")
        return generate_fallback_description(document)

def generate_fallback_description(document: Dict) -> str:
    """Generate a fallback description when ollama is unavailable."""
    description = f"Business located in {document['town']} specializing in {', '.join(document['categories'])}."
    logger.debug(f"Generated fallback description: {description}")
    return description

def generate_description(name: str, category_type: str, selected_categories: List[str], document: Dict) -> str:
    """Generate a description based on the document information using ollama."""
    logger.debug(f"Generating description for document: {json.dumps(document, indent=2)}")
    return get_ollama_description(document)

def generate_activity() -> Dict:
    """Generate a single commercial activity."""
    logger.debug("Generating new commercial activity")
    
    name, category_type, selected_categories = generate_business_info()
    
    # Create the document without description first
    document = {
        "taxCode": generate_tax_code(),
        "name": name,
        "town": random.choice(towns),
        "address": generate_address(),
        "categories": selected_categories
    }
    
    logger.debug(f"Generated initial document: {json.dumps(document, indent=2)}")
    
    # Get description from ollama based on the document
    document["description"] = generate_description(name, category_type, selected_categories, document)
    
    logger.debug(f"Final document with description: {json.dumps(document, indent=2)}")
    return document

def generate_batch(size: int = 100) -> List[Dict]:
    """Generate a batch of commercial activities."""
    logger.debug(f"Generating batch of {size} activities")
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
        logger.debug(f"Sending batch to {url}")
        logger.debug(f"Request headers: {headers}")
        logger.debug(f"Request payload sample (first activity): {json.dumps(activities[0], indent=2)}")
        
        response = requests.post(url, json=activities, headers=headers)
        response.raise_for_status()
        
        logger.debug(f"Batch sent successfully. Status code: {response.status_code}")
        return True
    except requests.exceptions.RequestException as e:
        logger.error(f"Error sending batch: {str(e)}")
        if hasattr(e.response, 'text'):
            logger.error(f"Response text: {e.response.text}")
        return False

def test_server_connection() -> bool:
    """Test if the server is running and accessible."""
    base_url = os.getenv("API_BASE_URL", "http://localhost:8080")
    try:
        logger.info(f"Testing server connection at {base_url} (API_BASE_URL environment variable)...")
        response = requests.get(base_url)
        logger.info(f"Server is accessible (status code: {response.status_code})")
        return True
    except requests.exceptions.ConnectionError:
        logger.error(f"Error: Could not connect to server at {base_url}")
        logger.error("Please ensure the server is running and the API_BASE_URL environment variable is set correctly.")
        return False

def main():
    parser = argparse.ArgumentParser(description='Generate and send commercial activities to the API')
    parser.add_argument('--test', action='store_true', help='Run in test mode with 2 activities')
    parser.add_argument('--batch-size', type=int, default=10, help='Batch size for sending activities (default: 10)')
    parser.add_argument('--total', type=int, default=10000, help='Total number of activities to generate (default: 10000)')
    parser.add_argument('--debug', action='store_true', help='Enable debug logging')
    args = parser.parse_args()
    
    if args.debug:
        logger.setLevel(logging.DEBUG)
    else:
        logger.setLevel(logging.INFO)

    logger.debug("Starting script with arguments: " + str(vars(args)))

    if not test_server_connection():
        sys.exit(1)

    if args.test:
        logger.info("Running in test mode (2 activities)...")
        test_batch = generate_batch(2)
        success = send_batch(test_batch)
        if not success:
            logger.error("Test batch failed.")
            sys.exit(1)
        logger.info("Test successful! Run without --test to generate all activities.")
        sys.exit(0)

    total_activities = args.total
    batch_size = args.batch_size
    num_batches = (total_activities + batch_size - 1) // batch_size

    start_time = time.time()
    activities_sent = 0
    logger.info(f"Generating and sending {total_activities:,} activities in batches of {batch_size}...")

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
        logger.info(f"Batch {batch_num + 1}/{num_batches} "
              f"({progress:.1f}% complete, "
              f"sending {len(batch)} activities)")
        logger.info(f"Rate: {rate:.0f} activities/sec, "
              f"ETA: {datetime.fromtimestamp(time.time() + eta).strftime('%H:%M:%S')}")
        
        if not send_batch(batch):
            logger.error(f"Failed at batch {batch_num + 1}. {activities_sent:,} activities were sent successfully.")
            sys.exit(1)
        
        activities_sent += len(batch)

    total_time = time.time() - start_time
    final_rate = activities_sent / total_time
    
    logger.info(f"Success! Sent {activities_sent:,} activities in {total_time:.1f} seconds")
    logger.info(f"Average rate: {final_rate:.0f} activities/second")

if __name__ == "__main__":
    main()