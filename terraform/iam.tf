resource "azuread_application" "github-cicd" {
  display_name = "github-cicd-test"
  owners       = [data.azuread_client_config.current.object_id]
}

resource "azuread_service_principal" "github-cicd" {
  client_id                    = azuread_application.github-cicd.client_id
  app_role_assignment_required = false
  use_existing                 = true
  owners                       = [data.azuread_client_config.current.object_id]

  feature_tags {
    enterprise = true
    gallery    = true
  }

  timeouts {
    read   = "300s"
    create = "600s"
  }
}
