/*
 * Copyright 2017 ING Bank N.V.
 * This file is part of the go-ethereum library.
 *
 * The go-ethereum library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The go-ethereum library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the go-ethereum library. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.kit.verifier.zeroknowledge.prover;

import com.kit.verifier.zeroknowledge.TTPGenerator;
import com.kit.verifier.zeroknowledge.dto.TTPMessage;
import com.kit.verifier.zeroknowledge.util.InputUtils;

import java.math.BigInteger;

/**
 * Generates the commitment
 */
public class TTPDemo {

    public void generateTrustedMessage(BigInteger x) {

        TTPMessage message = TTPGenerator.generateTTPMessage(x);

        String fileName = Config.getInstance().getProperty("ttpmessage.file.name");
        InputUtils.saveObject(fileName, message);

    }
}