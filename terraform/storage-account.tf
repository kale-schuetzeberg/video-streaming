resource "azurerm_storage_account" "storage_account" {
  name                     = var.app_name
  resource_group_name      = azurerm_resource_group.nodenavi.name
  location                 = var.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

resource "azurerm_storage_container" "storage_container" {
  name                  = var.app_name
  storage_account_name  = var.app_name
  container_access_type = "private"
}

