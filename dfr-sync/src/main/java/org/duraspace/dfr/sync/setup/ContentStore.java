package org.duraspace.dfr.sync.setup;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
/**
 * This class is just a temporary place holder.
 * TODO Replace with a primary content store instance.
 * @author Daniel Bernstein
 * 
 */
@Component
public class ContentStore {
    public List<String> getSpaces(){
        return Arrays.asList(new String[]{"myspace"});
    }
}
