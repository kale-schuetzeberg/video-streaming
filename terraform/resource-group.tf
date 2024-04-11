resource "azurerm_resource_group" "nodenavi" {
  name     = var.app_name
  location = var.location
}