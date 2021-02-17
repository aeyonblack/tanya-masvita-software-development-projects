using System;

/// <summary>
/// Models the base currency, Other currency types extend currency
/// </summary>
public class Currency 
{
    /// <summary>
    /// How much the player currently has
    /// </summary>
    public int currentAmount { get; set; }

    /// <summary>
    /// Invoked when currency changes
    /// </summary>
    public event Action currencyChanged;

    /// <summary>
    /// Initialize new instance of currency
    /// Should only be done once per game load
    /// </summary>
    /// <param name="player"></param>
    public Currency(int startingCurrency)
    {
        currentAmount = startingCurrency;
    }

    /// <summary>
    /// Increments the currency
    /// </summary>
    /// <param name="amount">Amount to increment by</param>
    public void AddCurrency(int amount)
    {
        ChangeCurrency(amount);
    }

    /// <summary>
    /// Tries to purchase an item with some cost
    /// Returns false if insufficient funds
    /// </summary>
    /// <param name="cost">Cost of item</param>
    /// <returns></returns>
    public bool TryPurchase(int cost)
    {
        if (!CanAfford(cost))
        {
            return false;
        }
        ChangeCurrency(-cost);
        return true;
    }

    public bool CanAfford(int cost)
    {
        return currentAmount >= cost;
    }

    /// <summary>
    /// Changes the currency
    /// </summary>
    /// <param name="amount">Amount to change currency by</param>
    public virtual void ChangeCurrency(int amount)
    {
        if (amount != 0 && (currentAmount + amount > 0))
        {
            currencyChanged?.Invoke();
        }
    }
}
