defmodule EnviroPulse.Repo.Migrations.AddFieldsFromGoogleProviderToUsers do
  use Ecto.Migration

  def change do
    alter table(:users) do
      add :name, :string
      add :given_name, :string
      add :family_name, :string
      add :picture, :string
      add :email_verified, :boolean
      add :locale, :string
    end
  end
end
