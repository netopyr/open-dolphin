/*
 * Copyright 2012-2013 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendolphin.core.client.inject

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import org.junit.Rule
import org.junit.Test
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.comm.ClientConnector
import org.opendolphin.core.client.comm.CommandBatcher
import org.opendolphin.core.client.comm.UiThreadHandler
import org.opendolphin.core.comm.Codec
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.JsonCodec

import javax.inject.Inject
import javax.inject.Singleton

class InjectionTests {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private Codec codec

    @Inject
    private UiThreadHandler uiThreadHandler

    @Inject
    private ClientModelStore clientModelStore

    @Inject
    private ClientConnector clientConnector

    @Inject
    private ClientDolphin clientDolphin

    @Test
    void assertBindings() {
        assert codec
        assert codec instanceof JsonCodec

        assert uiThreadHandler
        assert uiThreadHandler instanceof CustomUiThreadHandler

        assert clientDolphin

        assert clientModelStore

        assert clientConnector

        assert clientDolphin.clientModelStore == clientModelStore
        assert clientDolphin.clientConnector == clientConnector
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ClientDolphin).in(Singleton)
            bind(Codec).to(JsonCodec).in(Singleton)
            bind(UiThreadHandler).to(CustomUiThreadHandler).in(Singleton)
            bind(ClientModelStore).toProvider(ClientModelStoreProvider).in(Singleton)
            bind(ClientConnector).toProvider(CustomClientConnectorProvider).in(Singleton)
        }
    }

    private static class CustomUiThreadHandler implements UiThreadHandler {
        @Override
        void executeInsideUiThread(Runnable runnable) {
            runnable.run()
        }
    }

    private static class CustomClientConnectorProvider extends AbstractClientConnectorProvider {
        @Override
        protected ClientConnector instantiateClientConnector() {
            return new CustomClientConnector(clientDolphin)
        }
    }

    private static class CustomClientConnector extends ClientConnector {
        CustomClientConnector(ClientDolphin clientDolphin) {
            super(clientDolphin)
        }

        CustomClientConnector(ClientDolphin clientDolphin, CommandBatcher commandBatcher) {
            super(clientDolphin, commandBatcher)
        }

        @Override
        List<Command> transmit(List<Command> commands) {
            return null
        }
    }
}
