package com.github.gv2011.asn1;

/*-
 * %---license-start---
 * Vinz ASN.1
 * %
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %
 * Please note this should be read in the same way as the MIT license. (https://www.bouncycastle.org/licence.html)
 * 
 * Copyright (c) 2000-2015 The Legion of the Bouncy Castle Inc. (http://www.bouncycastle.org)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 * and associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * %---license-end---
 */
import static com.github.gv2011.util.bytes.ByteUtils.newBytesBuilder;

import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.BytesBuilder;

/**
 * An indefinite-length encoding version of an application specific object.
 */
public class BERApplicationSpecific
    extends ASN1ApplicationSpecific
{
    BERApplicationSpecific(
        final boolean isConstructed,
        final int tag,
        final Bytes octets)
    {
        super(isConstructed, tag, octets);
    }

    /**
     * Create an application specific object with a tagging of explicit/constructed.
     *
     * @param tag the tag number for this object.
     * @param object the object to be contained.
     */
    public BERApplicationSpecific(
        final int tag,
        final ASN1Encodable object)
    {
        this(true, tag, object);
    }

    /**
     * Create an application specific object with the tagging style given by the value of constructed.
     *
     * @param constructed true if the object is constructed.
     * @param tag the tag number for this object.
     * @param object the object to be contained.
     */
    public BERApplicationSpecific(
        final boolean constructed,
        final int tag,
        final ASN1Encodable object)
    {
        super(constructed || object.toASN1Primitive().isConstructed(), tag, getEncoding(constructed, object));
    }

    private static Bytes getEncoding(final boolean explicit, final ASN1Encodable object){
        final Bytes data = object.toASN1Primitive().getEncoded(ASN1Encoding.BER);

        if (explicit)
        {
            return data;
        }
        else
        {
//            final int lenBytes = getLengthOfHeader(data);
//            final byte[] tmp = new byte[data.length - lenBytes];
//            System.arraycopy(data, lenBytes, tmp, 0, tmp.length);
            return data.subList(getLengthOfHeader(data));
        }
    }

    /**
     * Create an application specific object which is marked as constructed
     *
     * @param tagNo the tag number for this object.
     * @param vec the objects making up the application specific object.
     */
    public BERApplicationSpecific(final int tagNo, final ASN1EncodableVector vec)
    {
        super(true, tagNo, getEncodedVector(vec));
    }

    private static Bytes getEncodedVector(final ASN1EncodableVector vec)
    {
        final BytesBuilder bOut = newBytesBuilder();
        for (int i = 0; i != vec.size(); i++){
          ((ASN1Object)vec.get(i)).getEncoded(ASN1Encoding.BER).write(bOut);
        }
        return bOut.build();
    }

    /* (non-Javadoc)
     * @see org.bouncycastle.asn1.ASN1Primitive#encode(org.bouncycastle.asn1.DEROutputStream)
     */
    @Override
    void encode(final ASN1OutputStream out)
    {
        int classBits = BERTags.APPLICATION;
        if (isConstructed)
        {
            classBits |= BERTags.CONSTRUCTED;
        }

        out.writeTag(classBits, tag);
        out.write(0x80);
        out.write(octets);
        out.write(0x00);
        out.write(0x00);
    }
}
