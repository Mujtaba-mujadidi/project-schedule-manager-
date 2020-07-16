package view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class GanttChart<X,Y> extends XYChart<X,Y> {

	private int blockHeight = 50; // Set default block height

	/**
	 * Constructor creates a new Gantt Chart with the given axis
	 * @param xAxis horizontal axis of the Gantt Chart
	 * @param yAxis vertical axis of the Gantt Chart
	 */
	public GanttChart(Axis<X> xAxis, Axis<Y> yAxis) {
		this(xAxis, yAxis, FXCollections.<Series<X,Y>>observableArrayList());
	}

	/**
	 * Constructor creates a new Gantt Chart with the given axis and ObservableList
	 * @param xAxis horizontal axis of the Gantt Chart
	 * @param yAxis vertical axis of the Gantt Chart
	 * @param observableArrayList ObservableList of data
	 */
	public GanttChart(Axis<X> xAxis, Axis<Y> yAxis,
			ObservableList<Series<X, Y>> observableArrayList) {
		super(xAxis, yAxis);
		if(!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
			throw new IllegalArgumentException("Incorrect axis type.");
		}
		setData(observableArrayList);
	}

	/**
	 * Class ExtraData for creating blocks on the Gantt Chart
	 */
	public static class ExtraData {
		public double length;
		public String style;
		/**
		 * Constructor takes a length and a style for a block.
		 * @param length length of the block
		 * @param style style of the block
		 */
		public ExtraData(double length, String style) {
			super();
			this.length = length;
			this.style = style;
		}
		/**
		 * Returns the length of the block
		 * @return length of block
		 */
		public double getLength() {
			return length;
		}
		/**
		 * Sets the length of a block
		 * @param length length of block
		 */
		public void setLength(double length) {
			this.length = length;
		}
		/**
		 * Returns the style of the block
		 * @return String containing the style of a block
		 */
		public String getStyle() {
			return style;
		}
		/**
		 * Sets the style of a block
		 * @param style String containing the style of a block
		 */
		public void setStyle(String style) {
			this.style = style;
		}


	}

	/**
	 * Resize the axis range in the case of the data being modified
	 */
	@Override
	protected void updateAxisRange() {
		Axis<X> xAxis = getXAxis();
		Axis<Y> yAxis = getYAxis();
		List<X> xData = null;
		List<Y> yData = null;
		if(xAxis.isAutoRanging()) { // Check if the axis needs to change the range
			xData = new ArrayList<X>();
		}
		if(yAxis.isAutoRanging()) { // Check if the axis needs to change the range
			yData = new ArrayList<Y>();
		}
		if(xData != null || yData != null) {
			for(Series<X,Y> series : getData()) { // Get current set of data
				for(Data<X,Y> data : series.getData()) {
					if(xData != null) {
						xData.add(data.getXValue());
						xData.add(xAxis.toRealValue(xAxis.toNumericValue(data.getXValue()) + getLength(data.getExtraValue()))); // Add the length of the block to the value of the X axis.
					}
					if(yData != null) {
						yData.add(data.getYValue()); // Add the value of the Y axis
					}
				}
			}
			if(xData != null) {
				xAxis.invalidateRange(xData); // Update range
			}
			if(yData != null) {
				yAxis.invalidateRange(yData); // Update range
			}
		}
	}
	/**
	 * Returns the length of a Block object provided
	 * @param extraValue Block object
	 * @return length of block given
	 */
	private double getLength(Object extraValue) {
		return ((ExtraData) extraValue).getLength();
	}
	/**
	 * Method called when a data item is added, creating the corresponding block.
	 */
	@Override
	protected void dataItemAdded(Series<X, Y> series, int item,
			Data<X, Y> data) {
		Node block = createBlock(series, getData().indexOf(series), data);
		getPlotChildren().add(block);

	}
	/**
	 * Method called when a data item is changed (this case does not need to be treated).
	 */
	@Override
	protected void dataItemChanged(Data<X, Y> arg0) {
		// TODO Auto-generated method stub

	}
	/**
	 * Method called when a data item is removed, removing the block and updating the display.
	 */
	@Override
	protected void dataItemRemoved(Data<X, Y> data,
			Series<X, Y> series) {
		Node block = data.getNode();
		getPlotChildren().remove(block);
		removeDataItemFromDisplay(series, data); // Update display

	}
	/**
	 * Plot children on the Gantt Chart
	 */
	@Override
	protected void layoutPlotChildren() {
		for(int i = 0; i < getData().size(); i++) { // Get all the sets of data.
			Series<X,Y> series = getData().get(i);
			Iterator<Data<X,Y>> it = getDisplayedDataIterator(series);
			while(it.hasNext()) { // Go through each set of data
				Data<X,Y> data = it.next();

				double x = getXAxis().getDisplayPosition(data.getXValue());
				double y = getYAxis().getDisplayPosition(data.getYValue());

				if(!(Double.isNaN(x) || Double.isNaN(y))) {
					Node block = data.getNode();
					Rectangle rect;
					if(block != null && block instanceof StackPane) {
						StackPane place = (StackPane)data.getNode();
						if(place.getShape() == null) {
							rect = new Rectangle(getLength(data.getExtraValue()), getBlockHeight()); // Create new rectangle block
						}
						else if(place.getShape() instanceof Rectangle) {
							rect = (Rectangle)place.getShape(); // Get the current block
						}
						else {
							return;
						}
						if(getXAxis() instanceof NumberAxis) {
							rect.setWidth(getLength(data.getExtraValue()) * Math.abs(((NumberAxis)getXAxis()).getScale())); // Set the width to be the width of the block times the scale, if the scale is numerical
						}
						else {
							rect.setWidth(getLength(data.getExtraValue())); // Set the width to be the width of the block
						}

						if(getYAxis() instanceof NumberAxis) { // Set the height to be the height of the block times the scale, if the scale is numerical
							rect.setHeight(getBlockHeight() * Math.abs(((NumberAxis)getYAxis()).getScale()));
						}
						else {
							rect.setHeight(getBlockHeight());
						}

						place.setShape(null);
						place.setShape(rect);
						place.setScaleShape(false);
						place.setCenterShape(false);
						place.setCacheShape(false);

						y -= getBlockHeight() / 2.0;
						block.setLayoutX(x);
						block.setLayoutY(y);
					}
				}
			}
		}

	}
	/**
	 * Create a Gantt Chart block with the given set of data
	 * @param series set of data for the Gantt Chart
	 * @param index index of data in the set of data
	 * @param data data for the Gantt Chart
	 * @return Node that represents the block in the Gantt Chart
	 */
	private Node createBlock(Series<X,Y> series, int index, Data<X,Y> data) {
		Node block = data.getNode(); // Get current data block

		if(block == null) {
			block = new StackPane(); // Create new block if it does not exist
			data.setNode(block);
		}
		block.setStyle(getStyle(data.getExtraValue()));
		return block; // Return the block

	}
	/**
	 * Returns the style of a given Block object.
	 * @param extraValue Block object to return style
	 * @return style of the block given
	 */
	private String getStyle(Object extraValue) {
		return ((ExtraData) extraValue).getStyle();
	}
	/**
	 * Returns the block height of a block.
	 * @return block height of the block
	 */
	private double getBlockHeight() {
		return blockHeight;
	}
	/**
	 * Sets the block height for a block.
	 * @param blockHeight block height of the block
	 */
	public void setBlockHeight(int blockHeight) {
		this.blockHeight = blockHeight;
	}
	/**
	 * Method called when a set of data is added, creating the corresponding blocks and adding them to the display.
	 */
	@Override
	protected void seriesAdded(Series<X, Y> series, int index) {
		for(int i = 0; i < series.getData().size(); i++) {
			Data<X,Y> data = series.getData().get(i);
			Node block = createBlock(series, index, data);
			getPlotChildren().add(block);
		}

	}
	/**
	 * Method called when a set of data is removed, removing the blocks and updating the display.
	 */
	@Override
	protected void seriesRemoved(Series<X, Y> series) {
		for(Data<X,Y> data : series.getData()) {
			Node block = data.getNode();
			getPlotChildren().remove(block);
		}
		removeSeriesFromDisplay(series); // Update display

	}

}
