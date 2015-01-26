# SQlite-Geo-fence
android geofence app

```
public class GeofenceBoundary {

	public static boolean PointInPolygon(Point point, Point[] _vertices) {
		int j = _vertices.length - 1;
		boolean oddNodes = false;

		for (int i = 0; i < _vertices.length; i++) {

			int retvalI = Double.compare(_vertices[i].Y, point.Y);
			
			int retvalJ = Double.compare(_vertices[j].Y, point.Y);
			
			double toComp = _vertices[i].X +(_vertices[i].X + (point.Y - _vertices[i].Y)
					/ (_vertices[j].Y - _vertices[i].Y)
					* (_vertices[j].X - _vertices[i].X));

			int comp = Double.compare(toComp, point.Y);

			if (retvalI < 0 && retvalI >= 0
					|| retvalJ < 0 && retvalI >= 0) {

				if (comp < 0) {
					oddNodes = !oddNodes;
				}
			}
			j = i;
		}

		return oddNodes;
	}
}
```
