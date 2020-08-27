/*
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
package org.xerial.snappy.pure;

import org.xerial.snappy.SnappyError;
import org.xerial.snappy.SnappyErrorCode;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteOrder;

import static java.lang.Short.reverseBytes;
import static java.lang.Integer.reverseBytes;
import static java.lang.Long.reverseBytes;

import static java.lang.String.format;

final class UnsafeUtil
{
    public static final Unsafe UNSAFE;
    private static final Field ADDRESS_ACCESSOR;
    private static ByteOrder order;
 
    private UnsafeUtil()
    {
    }

    static {
        //ByteOrder order = ByteOrder.nativeOrder();
        //if (!order.equals(ByteOrder.LITTLE_ENDIAN)) {
        //    throw new SnappyError(SnappyErrorCode.UNSUPPORTED_PLATFORM, format("pure-java snappy requires a little endian platform (found %s)", order));
        //} 
    	
    	order = ByteOrder.nativeOrder();

        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
        }
        catch (Exception e) {
            throw new SnappyError(SnappyErrorCode.UNSUPPORTED_PLATFORM, "pure-java snappy requires access to sun.misc.Unsafe");
        }

        try {
            Field field = Buffer.class.getDeclaredField("address");
            field.setAccessible(true);
            ADDRESS_ACCESSOR = field;
        }
        catch (Exception e) {
            throw new SnappyError(SnappyErrorCode.UNSUPPORTED_PLATFORM, "pure-java snappy requires access to java.nio.Buffer raw address field");
        }
    }

    public static long getAddress(Buffer buffer)
    {
        try {
            return (long) ADDRESS_ACCESSOR.get(buffer);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
 
    public static short getShort(Object inputBase, long offset)
    {
    	if (order.equals(ByteOrder.LITTLE_ENDIAN))
    		return UNSAFE.getShort(inputBase, (long)(offset));
    	else
    		return reverseBytes(UNSAFE.getShort(inputBase, (long)(offset)));   	
    }
    
    public static int getInt(Object inputBase, long offset)
    {
    	if (order.equals(ByteOrder.LITTLE_ENDIAN))
    		return UNSAFE.getInt(inputBase, (long)(offset));
    	else
    		return reverseBytes(UNSAFE.getInt(inputBase, (long)(offset)));   	
    }
    
    public static long getLong(Object inputBase, long offset)
    {
    	if (order.equals(ByteOrder.LITTLE_ENDIAN))
    		return UNSAFE.getLong(inputBase, (long)(offset));
    	else
    		return reverseBytes(UNSAFE.getLong(inputBase, (long)(offset)));
  	
    }

    public static void putShort(Object outputBase, long offset, short n)
    {
    	if (order.equals(ByteOrder.LITTLE_ENDIAN))
    	    UNSAFE.putShort(outputBase, offset, n);
    	else
    		UNSAFE.putShort(outputBase, offset, reverseBytes(n));  	
    }
    
    public static void putInt(Object outputBase, long offset, int n)
    {
    	if (order.equals(ByteOrder.LITTLE_ENDIAN))
    	    UNSAFE.putInt(outputBase, offset, n);
    	else
    		UNSAFE.putInt(outputBase, offset, reverseBytes(n));  	
    }
    
    public static void putLong(Object outputBase, long offset, long n)
    {
    	if (order.equals(ByteOrder.LITTLE_ENDIAN))
    		UNSAFE.putLong(outputBase, offset, n);
    	else
    		UNSAFE.putLong(outputBase, offset, reverseBytes(n));
  	
    }
}
