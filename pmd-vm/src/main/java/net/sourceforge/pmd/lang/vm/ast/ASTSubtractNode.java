
package net.sourceforge.pmd.lang.vm.ast;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Handles subtraction of nodes (in #set() )<br>
 * <br>
 *
 * Please look at the Parser.jjt file which is what controls the generation of
 * this class.
 *
 * @author <a href="mailto:wglass@forio.com">Will Glass-Husain</a>
 * @author <a href="mailto:pero@antaramusic.de">Peter Romianowski</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: ASTSubtractNode.java 691048 2008-09-01 20:26:11Z nbubna $
 */
public final class ASTSubtractNode extends ASTMathNode {

    ASTSubtractNode(int id) {
        super(id);
    }


    @Override
    public Object jjtAccept(VmParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
