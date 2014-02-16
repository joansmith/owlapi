/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/** Represents the Node ID for anonymous individuals
 * 
 * @author Matthew Horridge, The University of Manchester, Information
 *         Management Group
 * @since 3.0.0 */
public class NodeID implements Comparable<NodeID>, Serializable {
    private static final long serialVersionUID = 40000L;
    private static final AtomicLong COUNTER = new AtomicLong();
    private static final String NODE_ID_PREFIX = "genid";
    private static final String SHARED_NODE_ID_PREFIX = "genid-nodeid-";
    private static final String PREFIX = "_:";

    /** @param id
     *            the node id
     * @return string version of id */
    public static String nodeString(int id) {
        return NodeID.NODE_ID_PREFIX + Integer.toString(id);
    }

    /** @param id
     *            id
     * @return IRI with full node id */
    public static IRI nodeId(int id) {
        return IRI
                .create(PREFIX + NodeID.NODE_ID_PREFIX + Integer.toString(id));
    }

    /** Returns an absolute IRI from a nodeID attribute.
     * 
     * @param nodeID
     *            the node id
     * @return absolute IRI */
    public static String getIRIFromNodeID(String nodeID) {
        return PREFIX + SHARED_NODE_ID_PREFIX + nodeID.replace("genid", "");
    }

    /** Generates next anonymous IRI.
     * 
     * @return absolute IRI */
    public static String nextAnonymousIRI() {
        return PREFIX + NODE_ID_PREFIX + COUNTER.incrementAndGet();
    }

    /** Tests whether supplied IRI was generated by this parser in order to label
     * an anonymous node.
     * 
     * @param uri
     *            the IRI
     * @return {@code true} if the IRI was generated by this parser to label an
     *         anonymous node */
    public static boolean isAnonymousNodeIRI(String uri) {
        return uri != null
                && (uri.startsWith(PREFIX) || uri
                        .indexOf(NodeID.NODE_ID_PREFIX) != -1);
    }

    /** Tests whether supplied IRI was generated by this parser in order to label
     * an anonymous node.
     * 
     * @param iri
     *            the IRI
     * @return {@code true} if the IRI was generated by this parser to label an
     *         anonymous node */
    public static boolean isAnonymousNodeIRI(IRI iri) {
        return iri.getNamespace() != null
                && iri.getNamespace().contains(NodeID.NODE_ID_PREFIX);
    }

    /** @param iri
     *            the iri or node id
     * @return true if the iri is an anonymous label */
    public static boolean isAnonymousNodeID(String iri) {
        return iri != null && iri.indexOf(NodeID.SHARED_NODE_ID_PREFIX) > -1;
    }

    /** Gets a NodeID with a specific identifier string
     * 
     * @param id
     *            The String that identifies the node. If the String doesn't
     *            start with "_:" then this will be concatenated to the front of
     *            the specified id String; if the string is empty or null, an
     *            autogenerated id will be used.
     * @return A NodeID */
    public static NodeID getNodeID(String id) {
        String nonBlankId = id == null || id.length() == 0 ? PREFIX
                + NODE_ID_PREFIX + Long.toString(COUNTER.incrementAndGet())
                : id;
        return new NodeID(nonBlankId);
    }

    private final String id;

    private NodeID(String id) {
        if (id.startsWith(PREFIX)) {
            this.id = id;
        } else {
            this.id = PREFIX + id;
        }
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int compareTo(NodeID o) {
        return id.compareTo(o.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NodeID)) {
            return false;
        }
        NodeID other = (NodeID) obj;
        return id.equals(other.getID());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /** Gets the string representation of the node ID. This will begin with _:
     * 
     * @return The string representation of the node ID. */
    public String getID() {
        return id;
    }
}
