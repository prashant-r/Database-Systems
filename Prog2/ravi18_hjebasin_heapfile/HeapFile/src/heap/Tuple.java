package heap;

import java.util.Arrays;

public class Tuple {

    int length;
    byte[] data;
    int offset;
    
    @Override
	public String toString() {
		return "Tuple [length=" + length + ", data=" + Arrays.toString(data) + "]";
	}

	public Tuple(byte [] data , int offset, int length)
    {
        this.data = data;
        this.length = length;
        this.offset = 0;
    }

    public Tuple(byte[] data) {
        this.length = data.length;
        this.data = data;
    }

    public Tuple()
    {

    }
    public int getLength()
    {
        return this.length;
    }


    public  void setData(byte[] data)
    {
        this.data= data;
        this.length = this.data.length;
    }

    public byte[] getTupleByteArray() {
		byte [] datacopy = new byte [this.length];
		System.arraycopy(this.data, offset, datacopy, 0, this.length);
		return datacopy;
    }

    public void setlength(int tuple_length) {
        this.length = tuple_length;
    }


}