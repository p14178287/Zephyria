package com.almasb.zeph.combat;

/**
 * An essence when installed into a weapon, increases a character stat.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Essence implements java.io.Serializable {

    private static final long serialVersionUID = 4396825140388400662L;

    public final Stat stat;
    public final int bonus;

    public Essence(Stat stat, int bonus) {
        this.stat = stat;
        this.bonus = bonus;
    }

    @Override
    public String toString() {
        return stat + " +" + bonus;
    }
}
