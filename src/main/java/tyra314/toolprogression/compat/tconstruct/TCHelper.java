package tyra314.toolprogression.compat.tconstruct;

import net.minecraftforge.common.MinecraftForge;

public class TCHelper
{
    public static boolean isTCLoaded()
    {
        try
        {
            return Class.forName("slimeknights.tconstruct.TConstruct") != null;
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }

    public static void preInit()
    {
        MinecraftForge.EVENT_BUS.register(new TCEventHandler());
    }
}
