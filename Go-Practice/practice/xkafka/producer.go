package main

import (
	"context"

	"github.com/gojekfarm/xtools/xkafka"
	"github.com/rs/zerolog/log"
)

func publishMessages(messages []*xkafka.Message) {
	producer, err := xkafka.NewProducer(
		"test-seq-producer",
		xkafka.Brokers{brokers},
		xkafka.ErrorHandler(func(err error) error {
			log.Error().Err(err).Msg("")
			return err
		}),
	)
	if err != nil {
		panic(err)
	}

	defer producer.Close()

	ctx := context.Background()
	for _, msg := range messages {
		if err := producer.AsyncPublish(ctx, msg); err != nil {
			panic(err)
		}
	}
	log.Info().
		Int("count", len(messages)).
		Msg("[PRODUCER] published messages")
}
