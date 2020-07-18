# mpos-sample
## Step One
Add zibalsdk.aar file in app/libs directory.

## Step Two
Include below line in app level build.gradle.

```javascript
implementation files('libs/zibalsdk.aar');
```

## Step Three

```java
try {
    Intent intent = new Intent(MainActivity.this, ZibalActivity.class);
    intent.putExtra("zibalId",zibalId);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
} catch (Exception e) {
    Log.d("error happened", e.toString());
}
```


## Keep in mind
Minimun sdk version must be equal or greater than 16.
