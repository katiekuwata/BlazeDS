package blazeds.qa.remotingService;
import flex.graphics.ImageSnapshot;

public class ImageSnapshotType
{
    public String title;
    
    public ImageSnapshotType()
    { }
    
    public int setSnapShot(ImageSnapshot snap)
    {
        if (snap.getHeight() <= 0 ||
            snap.getWidth() <= 0 ||
            snap.getData().length <= 0 ||
            snap.getContentType().length() <= 0  )
        {
            throw new RuntimeException("ImageSnapshot can not have any zero length properties");
        }
        else
        {
            return snap.getData().length;
        }
    }
    
}
