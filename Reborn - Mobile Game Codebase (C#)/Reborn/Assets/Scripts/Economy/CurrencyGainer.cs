using System;
using UnityEngine;

public struct CurrencyChangeInfo
{
    /// <summary>
    /// Previous value of the currency
    /// </summary>
    public readonly int previousCurrency;

    /// <summary>
    /// Current value of the currency
    /// </summary>
    public readonly int currentCurrency;

    /// <summary>
    /// The difference in amount
    /// </summary>
    public readonly int difference;

    /// <summary>
    /// Gets the absolute difference in amount
    /// </summary>
    public readonly int absoluteDifference;

    /// <summary>
    /// Initialized the currency change info
    /// </summary>
    /// <param name="previous">Previous value of the currency</param>
    /// <param name="current">Current value of the currency</param>
    public CurrencyChangeInfo(int previous, int current)
    {
        previousCurrency = previous;
        currentCurrency = current;
        difference = currentCurrency - previousCurrency;
        absoluteDifference = Mathf.Abs(difference);
    }
}

public class CurrencyGainer : MonoBehaviour
{
    public int constantCurrencyAddition;

    public float constantCurrencyGainRate;

    public event Action<CurrencyChangeInfo> currencyChanged;

    protected RepeatingTimer m_GainTimer;

    public Currency currency { get; private set; }

    public void Initialize(Currency currencyController, int gainAddition, float gainRate)
    {
        constantCurrencyAddition = gainAddition;
        constantCurrencyGainRate = gainRate;
        Initialize(currencyController);
    }

    public void Initialize(Currency currencyController)
    {
        currency = currencyController;
        UpdateGainRate(constantCurrencyGainRate);
    }

    public void Tick(float deltaTime)
    {
        if  (m_GainTimer == null)
        {
            return;
        }
        m_GainTimer.Tick(Time.deltaTime);
    }

    public void UpdateGainRate(float currencyGainRate)
    {
        constantCurrencyGainRate = currencyGainRate;
        if (currencyGainRate < 0)
        {
            throw new ArgumentOutOfRangeException("currencyGainRate");
        }
        if (m_GainTimer == null)
        {
            m_GainTimer = new RepeatingTimer(1 / constantCurrencyGainRate, ConstantGain);
        }
        else
        {
            m_GainTimer.SetTime(1 / constantCurrencyGainRate);
        }
    }

    protected void ConstantGain()
    {
        int previousCurrency = currency.currentAmount;
        currency.AddCurrency(constantCurrencyAddition);
        int currentCurrency = currency.currentAmount;
        var info = new CurrencyChangeInfo(previousCurrency, currentCurrency);
        currencyChanged?.Invoke(info);
    }
}
