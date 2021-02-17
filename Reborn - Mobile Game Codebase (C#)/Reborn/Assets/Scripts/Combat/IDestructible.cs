using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

public interface IDestructible
{
    void OnDestruction(CharacterData player, CharacterStats enemy);
}
