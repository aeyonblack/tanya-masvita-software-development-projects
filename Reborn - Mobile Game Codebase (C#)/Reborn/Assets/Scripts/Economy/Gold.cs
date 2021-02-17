/// <summary>
/// Gold, extends currency
/// </summary>
public class Gold : Currency
{
    public Gold(int startingCurrency) : base(startingCurrency)
    {
    }

    public override void ChangeCurrency(int amount)
    {
        if (amount != 0 && (currentAmount + amount >= 0))
        {
            GameManager.instance.UpdateGold(amount);
        }
        base.ChangeCurrency(amount);
    }
}
