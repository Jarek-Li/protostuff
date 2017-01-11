//========================================================================
//Copyright (C) 2016 Alex Shvid
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package io.protostuff;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Custom eXtream IO util to serialize and deserialize MessagePack format
 * 
 * @author Alex Shvid
 *
 */

public final class MsgpackXIOUtil
{

    private MsgpackXIOUtil()
    {
    }

    public static <T> byte[] toByteArray(T message, Schema<T> schema, boolean numeric, LinkedBuffer buffer)
    {
        if (buffer.start != buffer.offset)
        {
            throw new IllegalArgumentException("Buffer previously used and had not been reset.");
        }

        MsgpackXOutput output = new MsgpackXOutput(buffer, numeric, schema);

        try
        {
            LinkedBuffer objectHeader = output.writeStartObject();
            schema.writeTo(output, message);
            output.writeEndObject(objectHeader);

        }
        catch (IOException e)
        {
            throw new RuntimeException("Serializing to a byte array threw an IOException", e);
        }

        return output.toByteArray();
    }

    /**
     * Serializes the {@code message} into a {@link LinkedBuffer} via {@link JsonXOutput} using the given {@code schema}
     * with the supplied buffer.
     */
    public static <T> void writeTo(LinkedBuffer buffer, T message, Schema<T> schema,
            boolean numeric)
    {
        if (buffer.start != buffer.offset)
            throw new IllegalArgumentException("Buffer previously used and had not been reset.");

        MsgpackXOutput output = new MsgpackXOutput(buffer, numeric, schema);
        
        try
        {
            LinkedBuffer objectStarter = output.writeStartObject();
            schema.writeTo(output, message);
            output.writeEndObject(objectStarter);
            
        }
        catch (IOException e)
        {
            throw new RuntimeException("Serializing to a byte array threw an IOException " +
                    "(should never happen).", e);
        }
    }
    
    /**
     * Serializes the {@code message} into an {@link OutputStream} via {@link MsgpackXOutput} with the supplied buffer.
     */
    public static <T extends Message<T>> void writeTo(OutputStream out, T message, boolean numeric,
            LinkedBuffer buffer)
            throws IOException
    {
        writeTo(out, message, message.cachedSchema(), numeric, buffer);
    }
    
    /**
     * Serializes the {@code message} into an {@link OutputStream} via {@link JsonXOutput} using the given
     * {@code schema}.
     */
    public static <T> void writeTo(OutputStream out, T message, Schema<T> schema, boolean numeric,
            LinkedBuffer buffer) throws IOException
    {
        if (buffer.start != buffer.offset)
        {
            throw new IllegalArgumentException("Buffer previously used and had not been reset.");
        }

        MsgpackXOutput output = new MsgpackXOutput(buffer, numeric, schema);
        
        LinkedBuffer objectHeader = output.writeStartObject();
        schema.writeTo(output, message);
        output.writeEndObject(objectHeader);

        LinkedBuffer.writeTo(out, buffer);
    }

}
