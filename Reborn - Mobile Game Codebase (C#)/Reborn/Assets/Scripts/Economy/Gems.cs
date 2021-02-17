/// <summary>
/// Gems- extends currency
/// </summary>
public class Gems : Currency
{
    public Gems(int startingCurrency) : base(startingCurrency)
    {
    }

    public override void ChangeCurrency(int amount)
    {
        if (amount != 0 && (currentAmount + amount > 0))
        {
            GameManager.instance.UpdateGems(amount);
        }
        base.ChangeCurrency(amount);
    }
}
