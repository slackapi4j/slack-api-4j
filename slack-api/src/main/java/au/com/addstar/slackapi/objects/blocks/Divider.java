package au.com.addstar.slackapi.objects.blocks;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created for the AddstarMC Project. Created by Narimm on 21/02/2019.
 */
@EqualsAndHashCode(callSuper=true)
public class Divider extends Block {
    public Divider() {
        super.setType(BlockType.DIVIDER);
    }

    /**
     * really just a place holder a divider has no Extensions from a regular block
     */
}
