package com.entitysync;

import com.entitysync.data.Brand;
import com.entitysync.data.CustomerOrder;
import com.entitysync.data.Product;
import com.entitysync.utils.SyncComparator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class SyncComparatorTest {

    @Test
    public void testComparator() {
        List<Class<?>> unsorted = Arrays.asList(CustomerOrder.class, Brand.class, Product.class);
        List<Class<?>> sorted = Arrays.asList(Brand.class, Product.class, CustomerOrder.class);
        unsorted.sort(new SyncComparator());
        assertThat(unsorted, is(sorted));
    }

}
