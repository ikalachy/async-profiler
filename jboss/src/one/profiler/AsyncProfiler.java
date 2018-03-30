/*
 * Copyright 2018 Andrei Pangin
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

package one.profiler;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Java API for in-process profiling. Serves as a wrapper around
 * async-profiler native library.
 * initiates loading of libasyncProfiler.so.
 *
 */
public class AsyncProfiler implements AsyncProfilerMBean {

    private static AsyncProfiler instance;

    private String libPath = null;

    public AsyncProfiler() { }

    @Override
    public void start(String event, long interval) {
        start0(event, interval);
    }

    @Override
    public void stop() {
        stop0();
    }

    @Override
    public native long getSamples();

    @Override
    public String execute(String command) {
        return execute0(command);
    }

    @Override
    public String dumpCollapsed(Counter counter) {
        return dumpCollapsed0(counter.ordinal());
    }

    @Override
    public String dumpTraces(int maxTraces) {
        String res = dumpTraces0(maxTraces);
        return res;
    }

    @Override
    public String dumpFlat(int maxMethods) {
        return dumpFlat0(maxMethods);
    }

    public String registerInstance() {

        ObjectInstance inst = null;

        try {
           inst = ManagementFactory.getPlatformMBeanServer().registerMBean(instance,
              new ObjectName("one.profiler:type=AsyncProfiler"));
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

        return String.valueOf(inst);
    }

    public static synchronized AsyncProfiler getInstance(String libPath) {
        if (instance != null) {
            return instance;
        }

        if (libPath == null) {
            System.loadLibrary("asyncProfiler");
        } else {
            System.load(libPath);
        }

        instance = new AsyncProfiler();

        return instance;
    }

    private native void start0(String event, long interval);
    private native void stop0();
    private native String execute0(String command);
    private native String dumpCollapsed0(int counter);
    private native String dumpTraces0(int maxTraces);
    private native String dumpFlat0(int maxMethods);

    public String getLibPath() {
        return libPath;
    }

    public void setLibPath(String libPath) {
        getInstance(libPath);
        this.libPath = libPath;
    }
}
