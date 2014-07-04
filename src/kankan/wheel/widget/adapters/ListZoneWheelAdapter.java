package kankan.wheel.widget.adapters;

import java.util.List;

import android.content.Context;

import com.imcore.common.model.City;
import com.imcore.common.model.Zone;

public class ListZoneWheelAdapter<T> extends AbstractWheelTextAdapter {

	 // items
   private List<Zone> items;

   /**
    * Constructor
    * @param context the current context
    * @param listProvince the items
    */
   public ListZoneWheelAdapter(Context context,  List<Zone> listZone) {
       super(context);
       
       //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
       this.items = listZone;
   }
   
   
   
   @Override
   public CharSequence getItemText(int index) {
      if (index >= 0 && index < items.size()) {
           String item = items.get(index).area;
           if (item instanceof CharSequence) {
               return (CharSequence) item;
           }
           return item.toString();
       }
       return null;
   }

   @Override
   public int getItemsCount() {
       return items.size();
   }
	
	
}
