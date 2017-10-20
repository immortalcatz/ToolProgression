package tyra314.toolprogression.compat.tconstruct;

import org.apache.logging.log4j.Level;
import tyra314.toolprogression.ToolProgressionMod;
import tyra314.toolprogression.config.ConfigHandler;
import tyra314.toolprogression.harvest.HarvestLevel;

import java.util.Map;

public class TiCMiningLevels
{
    public static Map<Integer, String> getMiningLevels()
    {
        Map<Integer, String> miningLevels = null;

        try
        {
            Class clazz = Class.forName("slimeknights.tconstruct.library.utils.HarvestLevels");
            if (clazz != null)
            {
                miningLevels = (Map<Integer, String>) clazz.getField("harvestLevelNames")
                        .get(null);
            }
        }
        catch (Exception e)
        {
            ToolProgressionMod.logger.info("Tinkers Construct not found.");
        }

        return miningLevels;
    }

    public static void overwriteMiningLevels()
    {
        if (!ConfigHandler.tconstruct_overwrite)
        {
            return;
        }

        Map<Integer, String> tcmininglevels = TiCMiningLevels.getMiningLevels();

        if (tcmininglevels == null)
        {
            ToolProgressionMod.logger.log(Level.WARN,
                    "Couldn't apply compat for TiC mining levels :(");

            return;
        }

        for (Map.Entry<Integer, String> it : tcmininglevels.entrySet())
        {
            int key = it.getKey();
            if (!HarvestLevel.levels.containsKey(key))
            {
                HarvestLevel.levels.put(key, new HarvestLevel(key, it.getValue()));
                ToolProgressionMod.logger.log(Level.INFO,
                        "Merged harvest level from Tinkers' Construct: " +
                        String.valueOf(key) +
                        " - " +
                        it.getValue());
            }
        }

        tcmininglevels.clear();

        for (HarvestLevel level : HarvestLevel.levels.values())
        {
            tcmininglevels.put(level.getLevel(), level.getFormatted());
        }

        ToolProgressionMod.logger.log(Level.INFO, "Applied compat for TiC mining levels");

    }

}
