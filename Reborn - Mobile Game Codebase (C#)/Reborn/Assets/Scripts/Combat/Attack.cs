/// <summary>
/// Extra Attack info
/// </summary>
public class Attack 
{
    private readonly int damage;
    private readonly bool critical;

    public Attack(int damage, bool critical)
    {
        this.damage = damage;
        this.critical = critical;
    }

    public int Damage
    {
        get { return damage; }
    }

    public bool IsCritical
    {
        get { return critical; }
    }
}
