package org.duraspace.dfr.app.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This IT augments the basic OCS ITs to extend the range of objects being
 * created for ingestion into the repository. This is a convenience used to
 * speed up creating suitable objects for repositories used as back ends for
 * applications.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/processorRepositoryIT-context.xml")
public class ProcessorRepositoryIT {

    private static final Logger logger =
        LoggerFactory.getLogger(ProcessorRepositoryIT.class);

    @Test
    public void testIngest() {

    }

}
