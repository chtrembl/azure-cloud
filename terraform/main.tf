terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "=4.1.0"
    }
    time = {
      source  = "hashicorp/time"
      version = "~> 0.9"
    }
    azuread = {
      source  = "hashicorp/azuread"
      version = "=3.6.0"
    }
  }

  required_version = ">= 1.1.0"
}

provider "azuread" {

}
provider "azurerm" {
  subscription_id     = "fd3a592d-8768-4193-bf0a-476f4c71bfa0"
  storage_use_azuread = true
  features {
  }
}

data "azuread_client_config" "current" {}
