package net.openhft.chronicle.queue.impl.single;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.ref.BinaryTwoLongReference;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.wire.Sequence;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jerry Shea on 22/11/17.
 */
@RunWith(Parameterized.class)
public class RollCycleEncodeSequenceTest {
    private final BinaryTwoLongReference longValue;
    private final RollCycleEncodeSequence rollCycleEncodeSequence;
    private final Bytes<ByteBuffer> bytes;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {RollCycles.DAILY},
                {RollCycles.HOURLY},
                {RollCycles.MINUTELY},
                {RollCycles.HUGE_DAILY}
        });
    }

    public RollCycleEncodeSequenceTest(final RollCycles cycle) {
        longValue = new BinaryTwoLongReference();
        bytes = Bytes.elasticByteBuffer();
        longValue.bytesStore(bytes, 0, 16);
        rollCycleEncodeSequence = new RollCycleEncodeSequence(longValue, cycle.defaultIndexCount(), cycle.defaultIndexSpacing());
    }

    @After
    public void before() {
        bytes.release();
    }

    @Test
    public void forWritePosition() {
        longValue.setOrderedValue(1);
        longValue.setOrderedValue2(2);
        // a cast to int of this magic number was causing problems
        long forWritePosition = 0x8001cc54L;
        long sequence = rollCycleEncodeSequence.getSequence(forWritePosition);
        assertEquals(Sequence.NOT_FOUND_RETRY, sequence);
    }
}
