package com.euronextclone.engine;

import com.euronextclone.MatchingUnit;
import com.euronextclone.OrderEntry;
import com.euronextclone.OrderSide;
import com.euronextclone.ordertypes.Limit;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Matching Engine will contain 1 or more matching units.
 *
 * Engine is responsible for listing to incoming requests, journal the requests (orderEntry),
 * replicate to slave Matching engine and then submitting request to Matching Unit for processing
 *
 * Engine also needs to heat-beat so slave can promote to master if required
 *
 * Engine will also handle networking - ZeroMQ
 */
public class MatchingEngine {
    private final static int RING_SIZE = 1024 * 8;
    private final ExecutorService EXECUTOR = Executors.newFixedThreadPool(3);
    private final RingBuffer<ValueEvent> ringBuffer;
    private final MatchingUnit matchingUnit;

    final EventHandler<ValueEvent> journalHandler = new EventHandler<ValueEvent>()
    {
        public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception
        {
            System.out.println(String.format("Journal %s", event.getValue()));
        }
    };
    final EventHandler<ValueEvent> replicatorHandler = new EventHandler<ValueEvent>()
    {
        public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception
        {
            System.out.println(String.format("Replicator %s", event.getValue()));
        }
    };
    final EventHandler<ValueEvent> matchingUnitHandler = new EventHandler<ValueEvent>()
    {
        public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception
        {
            System.out.println(String.format("Process %s", event.getValue()));
            matchingUnit.addOrder(event.getValue());
        }
    };

    private final Disruptor<ValueEvent> disruptor;

    public MatchingEngine(final String instrument) {
        matchingUnit = new MatchingUnit(instrument);

        disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, EXECUTOR,
                new SingleThreadedClaimStrategy(RING_SIZE),
                new SleepingWaitStrategy());
        disruptor.handleEventsWith(journalHandler, replicatorHandler).then(matchingUnitHandler);
        ringBuffer = disruptor.start();
    }

    public void publish(final int num) {
        // Publishers claim events in sequence
        long sequence = ringBuffer.next();
        ValueEvent event = ringBuffer.get(sequence);
        event.setValue(new OrderEntry(OrderSide.Buy, "A", num, new Limit(num))); // this could be more complex with multiple fields

        ringBuffer.publish(sequence);
    }

    public static void main(String args[]) {
        final MatchingEngine engine = new MatchingEngine("MSFT");
        engine.publish(1234);
        engine.publish(1235);

        System.out.println("[Enter] q to exit");
        Scanner scanner = new Scanner(System.in);
        while (!scanner.nextLine().trim().equals("q")) System.out.println("[Enter] q to exit");

        engine.halt();
        System.out.println("Exiting...");
    }

    private void halt() {
        disruptor.halt();
        disruptor.shutdown();
    }
}
