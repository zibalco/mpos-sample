# mpos-sample
## Step One
Add zibalsdk.aar file in app/libs directory.

## Step Two
Include below line in app level build.gradle.

```javascript
implementation files('libs/zibalsdk.aar');
```

## Step Three
Add These Lines in dependencies
```javascript
implementation 'androidx.appcompat:appcompat:1.1.0'
implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
implementation 'androidx.recyclerview:recyclerview:1.1.0'
implementation 'androidx.cardview:cardview:1.0.0'
```


## Step Four
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

## Step Five
Add these lines to AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```

## Response Types
| Code | Value | توضیحات |
 | :---:  | :----- | ----: |
 | 0 |  RESULT_DEVICE_CONNECTION_FAILED | .اتصال با دستگاه برقرار نشد
 | 1 |  RESULT_USER_CANCELED | .کاربر از پرداخت منصرف شده است
 | 2 |  RESULT_PAYMENT_SUCCESSFUL | .پرداخت با موفقیت انجام شد
 | 3 |  RESULT_ERROR_IN_PAYMENT | خطای عملیات پرداخت
 | 4 |  RESULT_ZIBAL_ID_ALREADY_PAID | .شناسه قبلا پرداخت شده است
 | 5 |  RESULT_INVALID_ZIBAL_ID | .شناسه زیبال نامعتبر است
 | 6 |  RESULT_UNREACHABLE_ZIBAL_SERVER | عدم دسترسی به سرور زیبال

**Example**
```java
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PAYMENT_REQUEST_CODE) {
        switch (resultCode) {
            case ZibalResponseEnum.RESULT_DEVICE_CONNECTION_FAILED:
                Toast.makeText(MainActivity.this,"اتصال با دستگاه برقرار نشد.",Toast.LENGTH_SHORT).show();
                break;
            case ZibalResponseEnum.RESULT_USER_CANCELED:
                Toast.makeText(MainActivity.this,"کاربر از پرداخت منصرف شده است",Toast.LENGTH_SHORT).show();
                break;
            case ZibalResponseEnum.RESULT_PAYMENT_SUCCESSFUL:
                Toast.makeText(MainActivity.this,"پرداخت با موفقیت انجام شد.",Toast.LENGTH_SHORT).show();
                break;
            case ZibalResponseEnum.RESULT_ERROR_IN_PAYMENT:
                Toast.makeText(MainActivity.this,"خطای عملیات پرداخت",Toast.LENGTH_SHORT).show();
                break;
            case ZibalResponseEnum.RESULT_ZIBAL_ID_ALREADY_PAID:
                Toast.makeText(MainActivity.this,"شناسه قبلا پرداخت شده.",Toast.LENGTH_SHORT).show();
                break;
            case ZibalResponseEnum.RESULT_INVALID_ZIBAL_ID:
                Toast.makeText(MainActivity.this,"شناسه زیبال نامعتبر است.",Toast.LENGTH_SHORT).show();
                break;
            case ZibalResponseEnum.RESULT_UNREACHABLE_ZIBAL_SERVER:
                Toast.makeText(MainActivity.this,"عدم دسترسی به سرور زیبال",Toast.LENGTH_SHORT).show();
                break;

        }
    }

}
```

## Return intent data
you can access these fields from returned data intent.

```javascript
//کد مرجع
data.getStringExtra("refNumber");
// شناسه پیگیری
data.getStringExtra("traceNumber");
//شناسه زیبال پرداخت شده
data.getStringExtra("paidZibalId");
```
paidZibalId in Configuration Payment is null.

## Keep in mind
Minimun sdk version must be equal or greater than 16.
