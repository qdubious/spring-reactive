/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.codec.support;

import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.test.TestSubscriber;

import org.springframework.http.MediaType;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Sebastien Deleuze
 */
public class Jaxb2EncoderTests extends AbstractAllocatingTestCase {

	private Jaxb2Encoder encoder;

	@Before
	public void createEncoder() {
		encoder = new Jaxb2Encoder();
	}

	@Test
	public void canEncode() {
		assertTrue(encoder.canEncode(null, MediaType.APPLICATION_XML));
		assertTrue(encoder.canEncode(null, MediaType.TEXT_XML));
		assertFalse(encoder.canEncode(null, MediaType.APPLICATION_JSON));
	}

	@Test
	public void encode() {
		Flux<Pojo> source = Flux.just(new Pojo("foofoo", "barbar"), new Pojo("foofoofoo", "barbarbar"));
		Flux<String> output = encoder.encode(source, allocator, null, null).map(chunk -> {
			byte[] b = new byte[chunk.readableByteCount()];
			chunk.read(b);
			return new String(b, StandardCharsets.UTF_8);
		});
		TestSubscriber<String> testSubscriber = new TestSubscriber<>();
		testSubscriber.bindTo(output)
				.assertValues("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><pojo><bar>barbar</bar><foo>foofoo</foo></pojo>",
							  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><pojo><bar>barbarbar</bar><foo>foofoofoo</foo></pojo>");
	}

}
