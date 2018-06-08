/*
 * Copyright © 2013-2018 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package global.namespace.fun.io.bios;

/**
 * Provides functions for {@link ThreadGroup}s.
 *
 * @author Christian Schlichtherle (copied and edited from TrueUpdate 0.8.1, where it was copied and edited from
 *                                  TrueCommons I/O 2.3.2)
 */
final class ThreadGroups {

    private ThreadGroups() { }

    /**
     * Determines a suitable thread group for <i>server threads</i> which provide shared services for one or more
     * otherwise unrelated <i>client threads</i>.
     * <p>
     * When a server thread gets spawned from a client thread and no particular thread group is selected, then it gets
     * inherited from the client thread to the server thread.
     * However, this may be inappropriate if the server thread has a longer life time than the client thread or if it's
     * shared by other client threads in different thread groups.
     * This method can then get used in order to determine a suitable thread group for the server thread.
     * <p>
     * This method searches for the top level accessible parent thread group by calling {@link #getThreadGroup()} and
     * walking up the parent thread group hierarchy until the next parent does not exist or is inaccessible.
     * If there is a security manager installed, then this method typically returns its directly associated thread group
     * because this is the only accessible one.
     * Otherwise, this method typically returns the root thread group.
     *
     * @return The result of the search for the top level accessible parent thread group, starting with the thread group
     *         of the security manager if installed or else the thread group of the current thread and walking up the
     *         parent thread group hierarchy until the next parent does not exist or is inaccessible.
     */
    static ThreadGroup getServerThreadGroup() {
        ThreadGroup tg = getThreadGroup();
        for (ThreadGroup ntg; null != (ntg = tg.getParent()); tg = ntg) {
            try {
                ntg.checkAccess();
            } catch (SecurityException ex) {
                break;
            }
        }
        return tg;
    }

    /**
     * Returns the thread group of the {@link System#getSecurityManager() security manager} if installed or else the
     * thread group of the current thread.
     */
    private static ThreadGroup getThreadGroup() {
        final SecurityManager sm = System.getSecurityManager();
        return null != sm ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }
}
