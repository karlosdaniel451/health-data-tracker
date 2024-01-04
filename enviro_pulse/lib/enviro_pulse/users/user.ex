defmodule EnviroPulse.Users.User do
  use Ecto.Schema
  use Pow.Ecto.Schema
  use PowAssent.Ecto.Schema

  @primary_key {:id, :binary_id, autogenerate: true}
  @foreign_key_type :binary_id
  schema "users" do
    # Fields provided by Google.
    field :name, :string
    field :given_name, :string
    field :family_name, :string
    field :picture, :string
    field :email_verified, :boolean
    field :locale, :string

    pow_user_fields()

    timestamps()
  end

  # It is required to populate the user struct with data fetched from the provider.
  # Source: https://hexdocs.pm/pow_assent/README.html#populate-fields
  def user_identity_changeset(user_or_changeset, user_identity, attrs, user_id_attrs) do
    user_or_changeset
    |> Ecto.Changeset.cast(attrs, [:name, :given_name, :family_name, :picture, :email_verified, :locale])
    |> pow_assent_user_identity_changeset(user_identity, attrs, user_id_attrs)
  end
end
