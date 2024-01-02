defmodule EnviroPulse.Repo do
  use Ecto.Repo,
    otp_app: :enviro_pulse,
    adapter: Ecto.Adapters.Postgres
end
