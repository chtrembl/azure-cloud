# Semantic Kernel Pet Pricing App

This Python application uses Semantic Kernel and Azure OpenAI to retrieve animal information. If the prompt requests pet product pricing, Semantic Kernel recognizes the function call schema from Azure OpenAI and calls an Azure Function to retrieve the product price, the same goes for Azure Pet Store Cart management.

## setup

python3 -m venv .venv
source .venv/bin/activate 
pip install --upgrade pip
pip install -r requirements.txt

## run

python3 src/app.py

## use

do dogs like to play?

how much is the ball?

please add the ball to my cart