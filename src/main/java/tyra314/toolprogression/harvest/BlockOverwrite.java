package tyra314.toolprogression.harvest;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.apache.logging.log4j.Level;
import tyra314.toolprogression.ToolProgressionMod;
import tyra314.toolprogression.config.ConfigHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class BlockOverwrite
{
    private static final String REGEX =
            "^(?<effective>\\?)?(?<toolclass>[^\\?=]+)=(?<level>-?\\d+)(@(?<hardness>.+))?$";

    private static final Pattern pattern;

    static
    {
        pattern = Pattern.compile(REGEX);
    }

    public String toolclass;
    public int level;
    public boolean toolRequired;
    public float hardness;

    public BlockOverwrite(String toolclass, int level, boolean toolRequired)
    {
        this.toolclass = toolclass;
        this.level = level;
        this.toolRequired = toolRequired;
        this.hardness = -1F;
    }

    public BlockOverwrite(String toolclass, int level, boolean toolRequired, float hardness)
    {
        this(toolclass, level, toolRequired);

        this.hardness = hardness;
    }

    public static void applyToAllStates(Block block)
    {
        for (IBlockState state : block.getBlockState().getValidStates())
        {
            applyToState(state);
        }
    }

    static void applyToState(IBlockState state)
    {
        String key = BlockHelper.getKeyString(state);

        BlockOverwrite overwrite = ConfigHandler.blockOverwrites.get(key);

        if (overwrite != null)
        {
            overwrite.apply(state);
        }
    }

    public static BlockOverwrite createFromConfig(String config)
    {
        Matcher matcher = pattern.matcher(config);

        if (!matcher.find())
        {
            return null;
        }

        boolean toolRequired = matcher.group("effective") == null;

        String toolClass = matcher.group("toolclass");
        int level = Integer.parseInt(matcher.group("level"));

        String hardness = matcher.group("hardness");

        if (hardness == null)
        {
            return new BlockOverwrite(toolClass, level, toolRequired);
        }
        else
        {
            return new BlockOverwrite(toolClass, level, toolRequired, Float.parseFloat(hardness));
        }
    }

    public String getConfig()
    {
        return (toolRequired ? "" : "?") + toolclass + "=" + String.valueOf(level);
    }

    public void addOverwrite(String toolClass, int level, boolean toolRequired)
    {
        this.toolclass = toolClass;
        this.level = level;
        this.toolRequired = toolRequired;
    }

    public void apply(IBlockState state)
    {
        if(hardness >= 0F)
        {
            state.getBlock().setHardness(hardness);
        }

        state.getBlock().setHarvestLevel(toolclass, level, state);

        ToolProgressionMod.logger.log(Level.INFO,
                String.format("Applying overwrite to block %s: %s %d",
                        BlockHelper.getKeyString(state),
                        toolclass,
                        level));
    }
}
