package kankan.wheel.widget.adapters;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.imcore.common.model.City;
import com.imcore.common.model.Province;

public class ListCityWheelAdapter<T> extends AbstractWheelTextAdapter {

	 // items
    private List<City> items;

    /**
     * Constructor
     * @param context the current context
     * @param listProvince the items
     */
    public ListCityWheelAdapter(Context context,  List<City> listCity) {
        super(context);
        
        //setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
        this.items = listCity;
    }
    
    
    
    @Override
    public CharSequence getItemText(int index) {
       if (index >= 0 && index < items.size()) {
            String item = items.get(index).city;
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
